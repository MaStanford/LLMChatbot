package com.stanford.chatapp.ui.chat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanford.chatapp.data.local.ChatMessage
import com.stanford.chatapp.data.local.ChatMessageDao
import com.stanford.chatapp.data.remote.ApiChatMessage
import com.stanford.chatapp.data.remote.ChatRequest
import com.stanford.chatapp.repositories.ChatRepository
import com.stanford.chatapp.repositories.SessionRepository
import com.stanford.chatapp.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val settingsRepository: SettingsRepository,
    private val chatRepository: ChatRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val userInput = mutableStateOf("")

    private val activeSessionId = settingsRepository.activeSessionId
        .onEach { id ->
            if (id == null) {
                val newSession = com.stanford.chatapp.data.local.Session(title = "New Chat")
                sessionRepository.insertSession(newSession)
                settingsRepository.setActiveSessionId(newSession.id)
            }
        }
        .filterNotNull()

    private val _streamingAssistantMessage = MutableStateFlow<ChatMessage?>(null)

    val messages: StateFlow<List<ChatMessage>> = activeSessionId
        .flatMapLatest { sessionId ->
            combine(
                chatMessageDao.getMessagesForSession(sessionId),
                _streamingAssistantMessage
            ) { history, streaming ->
                history + if (streaming != null && streaming.sessionId == sessionId) listOf(streaming) else emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableLlmProviders = settingsRepository.getAvailableLlmProviders()

    val selectedLlmProvider = settingsRepository.selectedLlmProvider.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "Gemini"
    )

    fun onUserInputChanged(input: String) {
        userInput.value = input
    }

    fun setSelectedLlmProvider(providerName: String) {
        viewModelScope.launch {
            settingsRepository.setSelectedLlmProvider(providerName)
        }
    }

    fun sendMessage() {
        Log.d("ChatViewModel", "sendMessage called")
        val messageText = userInput.value
        if (messageText.isNotBlank()) {
            viewModelScope.launch {
                val sessionId = activeSessionId.first()
                Log.d("ChatViewModel", "Session ID found: $sessionId")

                val currentSession = sessionRepository.getSession(sessionId)
                if (currentSession?.title == "New Chat") {
                    val title = messageText.split(" ").take(5).joinToString(" ")
                    sessionRepository.updateSession(currentSession.copy(title = title))
                }

                val userMessage = ChatMessage(
                    sessionId = sessionId,
                    role = "user",
                    content = messageText
                )
                chatMessageDao.insertMessage(userMessage)
                Log.d("ChatViewModel", "User message inserted into DB")
                userInput.value = ""

                val selectedProvider = selectedLlmProvider.first()
                Log.d("ChatViewModel", "Selected provider: $selectedProvider")

                val apiKey = when (selectedProvider) {
                    "Gemini" -> settingsRepository.geminiApiKey.first()
                    "OpenAI" -> settingsRepository.openAiApiKey.first()
                    "Grok" -> settingsRepository.grokApiKey.first()
                    else -> ""
                }
                Log.d("ChatViewModel", "Retrieved API Key: '$apiKey'")

                val model = when (selectedProvider) {
                    "Gemini" -> settingsRepository.geminiModel.first()
                    "OpenAI" -> settingsRepository.openAiModel.first()
                    "Grok" -> settingsRepository.grokModel.first()
                    else -> ""
                }

                if (apiKey.isBlank()) {
                    Log.e("ChatViewModel", "API key for $selectedProvider is blank")
                    val errorMessage = ChatMessage(
                        sessionId = sessionId,
                        role = "error",
                        content = "API key for $selectedProvider is not set. Please set it in the settings."
                    )
                    chatMessageDao.insertMessage(errorMessage)
                    return@launch
                }
                Log.d("ChatViewModel", "API key is present")

                val contextLimit = when (selectedProvider) {
                    "Gemini" -> settingsRepository.geminiContextLengthLimit.first()
                    "OpenAI" -> settingsRepository.openAiContextLengthLimit.first()
                    "Grok" -> settingsRepository.grokContextLengthLimit.first()
                    else -> 8000 // Default fallback
                }
                var currentContextLength = 0
                val limitedHistory = mutableListOf<ApiChatMessage>()

                // Add messages from the end until the context limit is reached
                for (message in messages.value.reversed().filter { it.role != "error" }) {
                    if (currentContextLength + message.content.length > contextLimit) {
                        break
                    }
                    limitedHistory.add(ApiChatMessage(message.role, message.content))
                    currentContextLength += message.content.length
                }

                // Reverse the list to restore chronological order
                var chatHistory = limitedHistory.reversed()

                // Ensure the new message is present, but not duplicated, to handle race condition.
                if (chatHistory.isEmpty() || chatHistory.last().content != messageText) {
                    chatHistory = chatHistory.plus(ApiChatMessage("user", messageText))
                }

                val request = ChatRequest(
                    model = model,
                    messages = chatHistory
                )

                val assistantMessageId = java.util.UUID.randomUUID().toString()
                _streamingAssistantMessage.value = ChatMessage(
                    id = assistantMessageId,
                    sessionId = sessionId,
                    role = "assistant",
                    content = "",
                    isLoading = true
                )

                Log.d("ChatViewModel", "Starting to collect stream from repository")
                chatRepository.getChatCompletionStream(request)
                    .flowOn(Dispatchers.IO)
                    .onEach { chunk ->
                        Log.d("ChatViewModel", "Collected chunk: $chunk")
                        if (chunk.error != null) {
                            val errorMessage = ChatMessage(
                                sessionId = sessionId,
                                role = "error",
                                content = chunk.error
                            )
                            chatMessageDao.insertMessage(errorMessage)
                            _streamingAssistantMessage.value = null
                        } else if (chunk.isFinished) {
                            _streamingAssistantMessage.value?.let {
                                chatMessageDao.insertMessage(it.copy(isLoading = false))
                            }
                            _streamingAssistantMessage.value = null
                            Log.d("ChatViewModel", "Finished collecting stream")
                        } else {
                            _streamingAssistantMessage.value = _streamingAssistantMessage.value?.copy(
                                content = _streamingAssistantMessage.value!!.content + (chunk.content ?: ""),
                                isLoading = false
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("ChatViewModel", "Error collecting stream", e)
                        val errorMessage = ChatMessage(
                            sessionId = sessionId,
                            role = "error",
                            content = "An error occurred: ${e.message}"
                        )
                        chatMessageDao.insertMessage(errorMessage)
                        _streamingAssistantMessage.value = null
                    }
                    .launchIn(viewModelScope)
            }
        } else {
            Log.d("ChatViewModel", "sendMessage skipped: message was blank")
        }
    }
}