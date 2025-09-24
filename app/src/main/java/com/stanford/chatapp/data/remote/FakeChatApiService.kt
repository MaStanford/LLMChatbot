package com.stanford.chatapp.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeChatApiService @Inject constructor() : ChatApiService {

    override fun getChatCompletionStream(request: ChatRequest): Flow<ChatResponseChunk> = flow {
        val fullResponse = "This is a streamed response from a fake LLM. It simulates the token-by-token generation of a real model."
        val chunks = fullResponse.split(" ").map { "$it " }

        for (chunk in chunks) {
            emit(ChatResponseChunk(content = chunk, isFinished = false))
            delay(100) // Simulate network latency
        }

        emit(ChatResponseChunk(content = null, isFinished = true))
    }
}
