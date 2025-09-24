package com.stanford.chatapp.utils

import kotlin.time.Duration
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

// A special object to signal a timeout event in our channel.
private object Timeout

/**
 * A custom Flow operator that buffers items and emits them as a chunk (List)
 * when either the buffer size reaches [count] or a [timeout] is reached
 * after the first item in the chunk arrives.
 *
 * @param count The max number of items per chunk.
 * @param timeout The duration to wait before emitting a chunk, even if it's not full.
 * @return A Flow that emits lists of items.
 */
fun <T> Flow<T>.chunked(count: Int, timeout: Duration): Flow<List<T>> = flow {
  // This buffer will hold the items for the current chunk.
  val buffer = mutableListOf<T>()

  // We use a channel to elegantly handle the timeout race condition.
  // It will receive either a new item from the upstream flow or a special Timeout signal.
  val channel = Channel<Any?>(Channel.CONFLATED)

  coroutineScope {
    // This coroutine will collect the original token stream.
    val upstreamJob = launch {
      collect { item -> channel.send(item) }
      // Once the upstream is done, we signal completion.
      channel.close()
    }

    // This coroutine manages the timeout.
    var timeoutJob: Job? = null

    // Consume from the channel until it's closed.
    for (item in channel) {
      if (item is Timeout) { // The timeout was triggered
        if (buffer.isNotEmpty()) {
          emit(buffer.toList()) // Emit the current buffer
          buffer.clear()
        }
        continue // Go back to waiting for the next item
      }

      // If we're here, it's a real item, not a timeout signal.
      @Suppress("UNCHECKED_CAST")
      buffer.add(item as T)

      if (buffer.size == 1) { // This is the first item in a new chunk
        // Start the timeout countdown.
        timeoutJob?.cancel()
        timeoutJob = launch {
          delay(timeout)
          channel.send(Timeout) // Send the timeout signal
        }
      }

      if (buffer.size >= count) { // The buffer is full
        timeoutJob?.cancel() // We don't need the timeout anymore
        emit(buffer.toList()) // Emit the full chunk
        buffer.clear()
      }
    }

    // The upstream has finished. If there's anything left in the buffer, emit it.
    timeoutJob?.cancel()
    if (buffer.isNotEmpty()) {
      emit(buffer.toList())
    }
    upstreamJob.cancel()
  }
}
