package com.stanford.chatapp.DI

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenAiApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GrokApi
