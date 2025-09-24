package com.stanford.chatapp.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

// Represents a single message in the chat history, for the API request
@Serializable
data class ApiChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

// Represents the request sent to the LLM API
data class ChatRequest(
    val model: String,
    val messages: List<ApiChatMessage>,
    val stream: Boolean = true
)

// Represents a single chunk of the streaming response
data class ChatResponseChunk(
    val content: String? = null,
    val isFinished: Boolean = false,
    val error: String? = null
)

/**
 * Interface for a service that communicates with an LLM API.
 */
interface ChatApiService {
    /**
     * Sends a chat history to the API and returns a stream of response chunks.
     *
     * @param request The chat request containing the model, messages, and stream flag.
     * @return A Flow that emits [ChatResponseChunk]s as they are received from the API.
     */
    fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk>
}