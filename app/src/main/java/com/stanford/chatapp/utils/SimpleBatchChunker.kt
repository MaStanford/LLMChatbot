package com.stanford.chatapp.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Implementation 1: Simple Batching Loop.
 * Uses a Channel and a loop to collect tokens and batch them.
 * Pros: Simple to understand.
 * Cons: Unlimited channel capacity can create memory pressure if consumer is slow.
 */
class SimpleBatchingChunker @Inject constructor() : TokenChunker {
  override fun chunkTokens(tokenStream: Flow<String>): Flow<String> = flow {
    coroutineScope {
      val channel = Channel<String>(capacity = Channel.UNLIMITED)

      launch {
        try {
          tokenStream.collect { token -> channel.send(token) }
        } finally {
          channel.close()
        }
      }

      val buffer = StringBuilder()
      val batchSize = 5
      var count = 0

      for (token in channel) {
        buffer.append(token)
        count++

        if (count >= batchSize) {
          emit(buffer.toString())
          buffer.clear()
          count = 0
        }
      }
      if (buffer.isNotEmpty()) {
        emit(buffer.toString())
      }
    }
  }
}