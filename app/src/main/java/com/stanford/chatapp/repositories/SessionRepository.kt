package com.stanford.chatapp.repositories

import com.stanford.chatapp.data.local.Session
import com.stanford.chatapp.data.local.SessionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) {
    fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
    }

    suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    suspend fun updateSession(session: Session) {
        sessionDao.updateSession(session)
    }

    suspend fun deleteSession(sessionId: String) {
        sessionDao.deleteSession(sessionId)
    }

    suspend fun getSession(sessionId: String): Session? {
        return sessionDao.getSession(sessionId)
    }
}
