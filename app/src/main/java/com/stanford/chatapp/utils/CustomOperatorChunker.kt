package com.stanford.chatapp.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implementation 2: Declarative Custom Operator (Recommended).
 * Uses our custom `chunked` Flow operator for the most efficient and declarative approach.
 */
class CustomOperatorChunker @Inject constructor() : TokenChunker {
  override fun chunkTokens(tokenStream: Flow<String>): Flow<String> {
    return tokenStream
      .chunked(count = 5, timeout = 100.milliseconds)
      .map { chunk -> chunk.joinToString("") }
  }
}