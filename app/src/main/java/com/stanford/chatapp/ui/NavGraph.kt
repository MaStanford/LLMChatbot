package com.stanford.chatapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stanford.chatapp.ui.chat.ChatScreen
import com.stanford.chatapp.ui.sessions.SessionScreen
import com.stanford.chatapp.ui.settings.SettingsScreen

object AppDestinations {
    const val CHAT_ROUTE = "chat"
    const val SETTINGS_ROUTE = "settings"
    const val SESSIONS_ROUTE = "sessions"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppDestinations.CHAT_ROUTE) {
        composable(AppDestinations.CHAT_ROUTE) {
            ChatScreen(navController = navController)
        }
        composable(AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(navController = navController)
        }
        composable(AppDestinations.SESSIONS_ROUTE) {
            SessionScreen(navController = navController)
        }
    }
}
