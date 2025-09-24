package com.stanford.chatapp.utils

import kotlinx.coroutines.flow.Flow

/**
 * Defines a strategy for transforming a stream of individual tokens
 * into a stream of concatenated, ready-to-display chunks.
 */
interface TokenChunker {
  fun chunkTokens(tokenStream: Flow<String>): Flow<String>
}