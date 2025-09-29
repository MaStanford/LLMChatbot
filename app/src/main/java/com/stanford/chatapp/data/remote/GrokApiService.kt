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
data class GrokRequest(
    val model: String,
    val messages: List<ApiChatMessage>,
    val stream: Boolean = true
)

@Serializable
data class GrokResponse(
    val choices: List<GrokChoice>
)

@Serializable
data class GrokChoice(
    val delta: GrokDelta? = null,
    val message: GrokMessage? = null
)

@Serializable
data class GrokDelta(
    val content: String?
)

@Serializable
data class GrokMessage(
    val content: String?
)

@Singleton
class GrokApiService @Inject constructor(
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository
) : ChatApiService {
    private val json = Json { ignoreUnknownKeys = true }

    private val grokApiUrl = "https://api.x.ai/v1/chat/completions"

    override fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> = flow {
        try {
            val apiKey = settingsRepository.grokApiKey.first()
            if (apiKey.isBlank()) {
                emit(ChatResponseChunk(error = "Grok API key is not set."))
                return@flow
            }

            val grokRequest = GrokRequest(
                model = request.model, // Assuming the model name is passed in the request
                messages = request.messages
            )

            val requestBody = json.encodeToString(GrokRequest.serializer(), grokRequest)
            Log.d("GrokApiService", "Request Body: $requestBody")

            val response = httpClient.post(grokApiUrl) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $apiKey")
                }
                setBody(grokRequest)
            }

            Log.d("GrokApiService", "Response Status: ${response.status}")
            Log.d("GrokApiService", "Response Headers: ${response.headers}")

            if (response.status.value != 200) {
                emit(ChatResponseChunk(error = "API Error: ${response.status.value} ${response.status.description}"))
                return@flow
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()
                Log.d("GrokApiService", "Raw line: $line") // Log every line
                if (line == null) continue

                if (line.startsWith("data: {")) {
                    val jsonString = line.substringAfter("data: ")
                    if (jsonString.contains("[DONE]")) {
                        break
                    }
                    try {
                        val grokResponse = json.decodeFromString<GrokResponse>(jsonString)
                        val content = grokResponse.choices.firstOrNull()?.delta?.content
                        if (content != null) {
                            emit(ChatResponseChunk(content = content))
                        }
                    } catch (e: Exception) {
                        Log.e("GrokApiService", "Error parsing JSON chunk: $jsonString", e)
                    }
                } else if (line.startsWith("{")) {
                    // Handle non-streaming response
                    try {
                        val grokResponse = json.decodeFromString<GrokResponse>(line)
                        val content = grokResponse.choices.firstOrNull()?.message?.content
                        if (content != null) {
                            emit(ChatResponseChunk(content = content))
                        }
                    } catch (e: Exception) {
                        Log.e("GrokApiService", "Error parsing non-streaming JSON: $line", e)
                    }
                    break // End after processing the single response
                }
            }
        } catch (e: Exception) {
            Log.e("GrokApiService", "Error fetching chat completion", e)
            emit(ChatResponseChunk(error = "Error: ${e.message}"))
        } finally {
            emit(ChatResponseChunk(isFinished = true))
        }
    }
}
