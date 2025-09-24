package com.stanford.chatapp.repositories

import com.stanford.chatapp.DI.GeminiApi
import com.stanford.chatapp.DI.OpenAiApi
import com.stanford.chatapp.DI.XaiApi
import com.stanford.chatapp.data.remote.ChatApiService
import com.stanford.chatapp.data.remote.ChatRequest
import com.stanford.chatapp.data.remote.ChatResponseChunk
import com.stanford.chatapp.data.remote.FakeChatApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    @OpenAiApi private val openAiApiService: ChatApiService,
    @GeminiApi private val geminiApiService: ChatApiService,
    @XaiApi private val xaiApiService: ChatApiService,
    private val settingsRepository: SettingsRepository
) {
    fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> {
        return kotlinx.coroutines.flow.flow {
            val selectedModel = settingsRepository.selectedLlmModel.first()
            val apiService = when (selectedModel) {
                "gemini-1.5-pro" -> geminiApiService
                "gpt-4" -> openAiApiService
                "xai" -> xaiApiService
                else -> geminiApiService // Default to Gemini
            }
            emitAll(apiService.getChatCompletionStream(request.copy(model = selectedModel)))
        }
    }
}
