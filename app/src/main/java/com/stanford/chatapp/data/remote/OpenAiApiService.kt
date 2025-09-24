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
data class OpenAiRequest(
    val model: String,
    val messages: List<ApiChatMessage>,
    val stream: Boolean = true
)

@Serializable
data class OpenAiResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val delta: Delta
)

@Serializable
data class Delta(
    val content: String?
)

@Singleton
class OpenAiApiService @Inject constructor(
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository
) : ChatApiService {
    private val json = Json { ignoreUnknownKeys = true }

    override fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> = flow {
        try {
            val apiKey = settingsRepository.openAiApiKey.first()
            if (apiKey.isBlank()) {
                emit(ChatResponseChunk(error = "OpenAI API key is not set."))
                return@flow
            }

            val url = "https://api.openai.com/v1/chat/completions"

            val openAiRequest = OpenAiRequest(
                model = request.model,
                messages = request.messages
            )

            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $apiKey")
                }
                setBody(openAiRequest)
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()
                if (line != null && line.startsWith("data: {")) {
                    val jsonString = line.substringAfter("data: ")
                    if (jsonString.contains("[DONE]")) {
                        break
                    }
                    val openAiResponse = json.decodeFromString<OpenAiResponse>(jsonString)
                    val content = openAiResponse.choices.firstOrNull()?.delta?.content
                    if (content != null) {
                        emit(ChatResponseChunk(content = content))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OpenAiApiService", "Error fetching chat completion", e)
            emit(ChatResponseChunk(error = "Error: ${e.message}"))
        } finally {
            emit(ChatResponseChunk(isFinished = true))
        }
    }
}
