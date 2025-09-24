package com.stanford.chatapp.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import javax.inject.Inject

/**
 * Implementation 3: Functional Scan Operator.
 * Uses the `scan` operator to manage chunking state functionally.
 * Pros: Uses standard Flow operators.
 * Cons: Logic can be complex; emits intermediate state objects which can be less efficient.
 */
class FunctionalScanChunker @Inject constructor() : TokenChunker {

  private data class ChunkingAccumulator(
    val chunkToEmit: List<String> = emptyList(),
    val buffer: List<String> = emptyList()
  )

  override fun chunkTokens(tokenStream: Flow<String>): Flow<String> {
    return tokenStream
      .scan(ChunkingAccumulator()) { acc, token ->
        val newBuffer = acc.buffer + token
        if (newBuffer.size >= 5) {
          ChunkingAccumulator(chunkToEmit = newBuffer, buffer = emptyList())
        } else {
          ChunkingAccumulator(chunkToEmit = emptyList(), buffer = newBuffer)
        }
      }
      .filter { it.chunkToEmit.isNotEmpty() }
      .map { it.chunkToEmit.joinToString("") }
  }
}