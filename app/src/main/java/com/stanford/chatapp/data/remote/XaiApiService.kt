package com.stanford.chatapp.data.remote

import android.util.Log
import com.stanford.chatapp.repositories.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class XaiRequest(
    val model: String,
    val messages: List<ApiChatMessage>,
    val stream: Boolean = true
)

@Serializable
data class XaiResponse(
    val choices: List<XaiChoice>
)

@Serializable
data class XaiChoice(
    val delta: XaiDelta
)

@Serializable
data class XaiDelta(
    val content: String?
)

@Singleton
class XaiApiService @Inject constructor(
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository
) : ChatApiService {
    private val json = Json { ignoreUnknownKeys = true }

    // IMPORTANT: Replace this with the actual Grok API endpoint
    private val xaiApiUrl = "https://api.xai.com/v1/chat/completions"

    override fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> = flow {
        try {
            val apiKey = settingsRepository.xaiApiKey.first()
            if (apiKey.isBlank()) {
                emit(ChatResponseChunk(error = "XAI API key is not set."))
                return@flow
            }

            val xaiRequest = XaiRequest(
                model = request.model, // Assuming the model name is passed in the request
                messages = request.messages
            )

            val response = httpClient.post(xaiApiUrl) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $apiKey")
                }
                setBody(xaiRequest)
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()
                if (line != null && line.startsWith("data: {")) {
                    val jsonString = line.substringAfter("data: ")
                    if (jsonString.contains("[DONE]")) {
                        break
                    }
                    val xaiResponse = json.decodeFromString<XaiResponse>(jsonString)
                    val content = xaiResponse.choices.firstOrNull()?.delta?.content
                    if (content != null) {
                        emit(ChatResponseChunk(content = content))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("XaiApiService", "Error fetching chat completion", e)
            emit(ChatResponseChunk(error = "Error: ${e.message}"))
        } finally {
            emit(ChatResponseChunk(isFinished = true))
        }
    }
}
