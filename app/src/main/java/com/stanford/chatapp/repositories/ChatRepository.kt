package com.stanford.chatapp.repositories

import com.stanford.chatapp.DI.GeminiApi
import com.stanford.chatapp.DI.GrokApi
import com.stanford.chatapp.DI.OpenAiApi
import com.stanford.chatapp.data.remote.ChatApiService
import com.stanford.chatapp.data.remote.ChatRequest
import com.stanford.chatapp.data.remote.ChatResponseChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class ChatRepository @Inject constructor(
    @OpenAiApi private val openAiApiService: ChatApiService,
    @GeminiApi private val geminiApiService: ChatApiService,
    @GrokApi private val grokApiService: ChatApiService,
    private val settingsRepository: SettingsRepository
) {
    fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> {
        return kotlinx.coroutines.flow.flow {
            val selectedProvider = settingsRepository.selectedLlmProvider.first()
            val apiService = when (selectedProvider) {
                "Gemini" -> geminiApiService
                "OpenAI" -> openAiApiService
                "Grok" -> grokApiService
                else -> geminiApiService // Default to Gemini
            }
            Log.d("ChatRepository", "Selected Provider: $selectedProvider, Model: ${request.model}")
            emitAll(apiService.getChatCompletionStream(request))
        }
    }
}
