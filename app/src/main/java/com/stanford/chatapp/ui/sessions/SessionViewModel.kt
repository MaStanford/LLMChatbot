package com.stanford.chatapp.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanford.chatapp.data.local.Session
import com.stanford.chatapp.repositories.SessionRepository
import com.stanford.chatapp.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val sessions = sessionRepository.getAllSessions().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun onSessionSelected(sessionId: String) {
        viewModelScope.launch {
            settingsRepository.setActiveSessionId(sessionId)
        }
    }

    fun onDeleteSession(sessionId: String) {
        viewModelScope.launch {
            sessionRepository.deleteSession(sessionId)
        }
    }

    fun onNewSession() {
        viewModelScope.launch {
            val newSession = Session(title = "New Chat")
            sessionRepository.insertSession(newSession)
            settingsRepository.setActiveSessionId(newSession.id)
        }
    }

    fun renameSession(session: Session, newTitle: String) {
        viewModelScope.launch {
            sessionRepository.updateSession(session.copy(title = newTitle))
        }
    }
}
