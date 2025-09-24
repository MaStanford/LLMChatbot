package com.stanford.chatapp.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val SELECTED_LLM_MODEL = stringPreferencesKey("selected_llm_model")
        val ACTIVE_SESSION_ID = stringPreferencesKey("active_session_id")
        val THEME = stringPreferencesKey("theme")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        val XAI_API_KEY = stringPreferencesKey("xai_api_key")
        val GEMINI_CONTEXT_LENGTH_LIMIT = intPreferencesKey("gemini_context_length_limit")
        val OPENAI_CONTEXT_LENGTH_LIMIT = intPreferencesKey("openai_context_length_limit")
        val XAI_CONTEXT_LENGTH_LIMIT = intPreferencesKey("xai_context_length_limit")
    }

    val geminiContextLengthLimit: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GEMINI_CONTEXT_LENGTH_LIMIT] ?: 8000
    }

    suspend fun setGeminiContextLengthLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_CONTEXT_LENGTH_LIMIT] = limit
        }
    }

    val openAiContextLengthLimit: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.OPENAI_CONTEXT_LENGTH_LIMIT] ?: 8000
    }

    suspend fun setOpenAiContextLengthLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPENAI_CONTEXT_LENGTH_LIMIT] = limit
        }
    }

    val xaiContextLengthLimit: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.XAI_CONTEXT_LENGTH_LIMIT] ?: 8000
    }

    suspend fun setXaiContextLengthLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.XAI_CONTEXT_LENGTH_LIMIT] = limit
        }
    }

    val theme: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME] ?: "SYSTEM_DEFAULT"
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    val selectedLlmModel: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_LLM_MODEL] ?: "gemini-1.5-pro"
    }

    suspend fun setSelectedLlmModel(modelName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LLM_MODEL] = modelName
        }
    }

    val activeSessionId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACTIVE_SESSION_ID]
    }

    suspend fun setActiveSessionId(sessionId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_SESSION_ID] = sessionId
        }
    }

    val geminiApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GEMINI_API_KEY] ?: ""
    }

    suspend fun setGeminiApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_API_KEY] = apiKey
        }
    }

    val openAiApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.OPENAI_API_KEY] ?: ""
    }

    suspend fun setOpenAiApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPENAI_API_KEY] = apiKey
        }
    }

    val xaiApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.XAI_API_KEY] ?: ""
    }

    suspend fun setXaiApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.XAI_API_KEY] = apiKey
        }
    }

    fun getAvailableLlmModels(): List<String> {
        return listOf("gemini-1.5-pro", "gpt-4", "xai")
    }
}
