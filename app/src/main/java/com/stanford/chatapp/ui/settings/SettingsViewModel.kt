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

    val geminiApiKey = settingsRepository.geminiApiKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val openAiApiKey = settingsRepository.openAiApiKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val xaiApiKey = settingsRepository.xaiApiKey.stateIn(
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

    val xaiContextLengthLimit = settingsRepository.xaiContextLengthLimit.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        8000
    )

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
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

    fun setXaiApiKey(apiKey: String) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "Saving XAI API Key: $apiKey")
            settingsRepository.setXaiApiKey(apiKey)
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

    fun setXaiContextLengthLimit(limit: Int) {
        viewModelScope.launch {
            settingsRepository.setXaiContextLengthLimit(limit)
        }
    }
}
