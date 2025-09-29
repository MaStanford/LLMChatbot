package com.stanford.chatapp.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanford.chatapp.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme = settingsRepository.theme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "SYSTEM_DEFAULT"
    )

    val selectedLlmProvider = settingsRepository.selectedLlmProvider.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "Gemini"
    )

    val geminiApiKey = settingsRepository.geminiApiKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    fun setSelectedLlmProvider(provider: String) {
        viewModelScope.launch {
            settingsRepository.setSelectedLlmProvider(provider)
        }
    }


    fun setGeminiApiKey(apiKey: String) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Saving Gemini API Key: $apiKey")
            settingsRepository.setGeminiApiKey(apiKey)
        }
    }

    fun setOpenAiApiKey(apiKey: String) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Saving OpenAI API Key: $apiKey")
            settingsRepository.setOpenAiApiKey(apiKey)
        }
    }

    fun setGrokApiKey(apiKey: String) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Saving Grok API Key: $apiKey")
            settingsRepository.setGrokApiKey(apiKey)
        }
    }

    fun setGeminiContextLengthLimit(limit: Int) {
        viewModelScope.launch {
            settingsRepository.setGeminiContextLengthLimit(limit)
        }
    }

    fun setOpenAiContextLengthLimit(limit: Int) {
        viewModelScope.launch {
            settingsRepository.setOpenAiContextLengthLimit(limit)
        }
    }

    fun setGrokContextLengthLimit(limit: Int) {
        viewModelScope.launch {
            settingsRepository.setGrokContextLengthLimit(limit)
        }
    }

    fun setGeminiModel(model: String) {
        viewModelScope.launch {
            settingsRepository.setGeminiModel(model)
        }
    }

    fun setOpenAiModel(model: String) {
        viewModelScope.launch {
            settingsRepository.setOpenAiModel(model)
        }
    }

    fun setGrokModel(model: String) {
        viewModelScope.launch {
            settingsRepository.setGrokModel(model)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    val openAiApiKey = settingsRepository.openAiApiKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val grokApiKey = settingsRepository.grokApiKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val geminiContextLengthLimit = settingsRepository.geminiContextLengthLimit.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        8000
    )

    val openAiContextLengthLimit = settingsRepository.openAiContextLengthLimit.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        8000
    )

    val grokContextLengthLimit = settingsRepository.grokContextLengthLimit.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        8000
    )

    val geminiModel = settingsRepository.geminiModel.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "gemini-2.5-pro"
    )

    val openAiModel = settingsRepository.openAiModel.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "gpt-4"
    )

    val grokModel = settingsRepository.grokModel.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "grok-3"
    )
}
