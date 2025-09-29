package com.stanford.chatapp.ui.settings

import androidx.compose.runtime.Stable

@Stable
data class ModelProvider(
    val name: String,
    val models: List<String>,
    val defaultModel: String
)

val modelProviders = listOf(
    ModelProvider(
        name = "Gemini",
        models = listOf(
            "gemini-2.5-pro",
            "gemini-1.5-pro-latest",
            "gemini-pro",
            "gemini-pro-vision"
        ),
        defaultModel = "gemini-2.5-pro"
    ),
    ModelProvider(
        name = "OpenAI",
        models = listOf(
            "gpt-4-1106-preview",
            "gpt-4",
            "gpt-3.5-turbo-1106"
        ),
        defaultModel = "gpt-4-1106-preview"
    ),
    ModelProvider(
        name = "Grok",
        models = listOf(
            "grok-3",
            "grok-code-fast-1",
            "grok-3-mini"
        ),
        defaultModel = "grok-1"
    )
)
