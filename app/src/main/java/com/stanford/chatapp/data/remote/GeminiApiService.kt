package com.stanford.chatapp.data.remote

import android.util.Log
import com.stanford.chatapp.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import okio.IOException

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig = GenerationConfig()
)
@Serializable
data class Content(
    val parts: List<Part>,
    val role: String
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double = 0.9,
    val topK: Int = 1,
    val topP: Double = 1.0,
    val maxOutputTokens: Int = 2048,
    val stopSequences: List<String> = emptyList()
)

// Data classes for the streaming response
@Serializable
data class GeminiStreamResponse(
    val candidates: List<StreamCandidate>
)

@Serializable
data class StreamCandidate(
    val content: Content
)

@Singleton
class GeminiApiServiceImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val settingsRepository: SettingsRepository
) : ChatApiService {
    private val json = Json { ignoreUnknownKeys = true }

    override fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> = flow {
        val apiKey = settingsRepository.geminiApiKey.first()
        if (apiKey.isBlank()) {
            emit(ChatResponseChunk(error = "Gemini API key is not set."))
            return@flow
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/${request.model}:streamGenerateContent"
        Log.d("GeminiApiService", "Request URL: $url")

        val mappedMessages = request.messages
            .filter { it.role == "user" || it.role == "assistant" }
            .map {
                val role = if (it.role == "assistant") "model" else "user"
                Content(role = role, parts = listOf(Part(text = it.content)))
            }

        val geminiRequest = GeminiRequest(contents = mappedMessages)
        val requestBody = json.encodeToString(GeminiRequest.serializer(), geminiRequest)
        Log.d("GeminiApiService", "Request Body: $requestBody")

        val httpRequest = Request.Builder()
            .url(url)
            .addHeader("x-goog-api-key", apiKey)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(httpRequest).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code}: ${response.message}")
        }

        val source = response.body!!.source()
        val buffer = okio.Buffer()
        var braceCount = 0
        var inString = false

        // The Gemini API streams a JSON array, so we need to handle the opening and closing brackets
        // and the commas between objects.
        // Skip until the first '{'
        while (!source.exhausted()) {
            val byte = source.readByte()
            if (byte.toInt().toChar() == '{') {
                buffer.writeByte(byte.toInt())
                braceCount++
                break
            }
        }

        while (!source.exhausted()) {
            val byte = source.readByte()
            val char = byte.toInt().toChar()
            buffer.writeByte(byte.toInt())

            if (char == '"') {
                // This is a simplification and doesn't handle escaped quotes perfectly,
                // but it's good enough for this known response format.
                var backslashCount = 0
                var index = buffer.size - 2
                while (index >= 0 && buffer[index].toInt().toChar() == '\\') {
                    backslashCount++
                    index--
                }
                if (backslashCount % 2 == 0) {
                    inString = !inString
                }
            }

            if (!inString) {
                if (char == '{') {
                    braceCount++
                } else if (char == '}') {
                    braceCount--
                    if (braceCount == 0) {
                        // We have a complete JSON object in the buffer
                        val jsonString = buffer.readUtf8()
                        buffer.clear()

                        try {
                            val responseObject = json.decodeFromString<GeminiStreamResponse>(jsonString)
                            val text = responseObject.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                            if (!text.isNullOrEmpty()) {
                                emit(ChatResponseChunk(content = text))
                            }
                        } catch (e: Exception) {
                            Log.e("GeminiApiService", "Error parsing JSON chunk: $jsonString", e)
                            // Continue to the next chunk
                        }

                        // Skip until the next '{'
                        while (!source.exhausted()) {
                            val nextByte = source.readByte()
                            if (nextByte.toInt().toChar() == '{') {
                                buffer.writeByte(nextByte.toInt())
                                braceCount++
                                break
                            }
                        }
                    }
                }
            }
        }
    }.onCompletion {
        // This will be called when the flow completes normally or is cancelled.
        emit(ChatResponseChunk(isFinished = true))
    }
}
