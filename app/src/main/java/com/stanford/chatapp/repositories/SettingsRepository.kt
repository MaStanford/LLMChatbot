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
        val SELECTED_LLM_PROVIDER = stringPreferencesKey("selected_llm_provider")
        val ACTIVE_SESSION_ID = stringPreferencesKey("active_session_id")
        val THEME = stringPreferencesKey("theme")

        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        val GROK_API_KEY = stringPreferencesKey("grok_api_key")

        val GEMINI_CONTEXT_LENGTH_LIMIT = intPreferencesKey("gemini_context_length_limit")
        val OPENAI_CONTEXT_LENGTH_LIMIT = intPreferencesKey("openai_context_length_limit")
        val GROK_CONTEXT_LENGTH_LIMIT = intPreferencesKey("grok_context_length_limit")

        val GEMINI_MODEL = stringPreferencesKey("gemini_model")
        val OPENAI_MODEL = stringPreferencesKey("openai_model")
        val GROK_MODEL = stringPreferencesKey("grok_model")
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

    val grokContextLengthLimit: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GROK_CONTEXT_LENGTH_LIMIT] ?: 8000
    }

    suspend fun setGrokContextLengthLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROK_CONTEXT_LENGTH_LIMIT] = limit
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

    val selectedLlmProvider: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_LLM_PROVIDER] ?: "Gemini"
    }

    suspend fun setSelectedLlmProvider(providerName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LLM_PROVIDER] = providerName
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

    val grokApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GROK_API_KEY] ?: ""
    }

    suspend fun setGrokApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROK_API_KEY] = apiKey
        }
    }

    val geminiModel: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GEMINI_MODEL] ?: "gemini-2.5-pro"
    }

    suspend fun setGeminiModel(model: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_MODEL] = model
        }
    }

    val openAiModel: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.OPENAI_MODEL] ?: "gpt-4"
    }

    suspend fun setOpenAiModel(model: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPENAI_MODEL] = model
        }
    }

    val grokModel: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GROK_MODEL] ?: "grok-3"
    }

    suspend fun setGrokModel(model: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROK_MODEL] = model
        }
    }

    fun getAvailableLlmProviders(): List<String> {
        return listOf("Gemini", "OpenAI", "Grok")
    }
}
