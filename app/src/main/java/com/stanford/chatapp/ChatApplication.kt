package com.stanford.chatapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The Application class for the app. It serves as the entry point for Hilt's
 * dependency injection.
 *
 * The @HiltAndroidApp annotation is crucial. It triggers Hilt's code generation,
 * which creates a dependency container attached to the application's lifecycle.
 */
@HiltAndroidApp
class ChatApplication : Application()
