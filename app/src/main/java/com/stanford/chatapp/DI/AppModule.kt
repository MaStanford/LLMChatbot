package com.stanford.chatapp.DI

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.stanford.chatapp.data.local.AppDatabase
import com.stanford.chatapp.data.local.ChatMessageDao
import com.stanford.chatapp.data.local.MIGRATION_1_2
import com.stanford.chatapp.data.local.MIGRATION_2_3
import com.stanford.chatapp.data.local.MIGRATION_3_4
import com.stanford.chatapp.data.local.SessionDao
import com.stanford.chatapp.data.remote.ChatApiService
import com.stanford.chatapp.data.remote.GeminiApiServiceImpl
import com.stanford.chatapp.data.remote.OpenAiApiService
import com.stanford.chatapp.data.remote.XaiApiService
import com.stanford.chatapp.repositories.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.userPreferencesStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @OpenAiApi
    fun provideOpenAiApiService(openAiApiService: OpenAiApiService): ChatApiService = openAiApiService

    @Provides
    @Singleton
    @GeminiApi
    fun provideGeminiApiService(geminiApiService: GeminiApiServiceImpl): ChatApiService = geminiApiService

    @Provides
    @Singleton
    @XaiApi
    fun provideXaiApiService(xaiApiService: XaiApiService): ChatApiService = xaiApiService

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userPreferencesStore
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository {
        return SettingsRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "chat.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}