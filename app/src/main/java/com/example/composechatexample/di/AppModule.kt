package com.example.composechatexample.di

import android.content.Context
import android.content.SharedPreferences
import com.example.composechatexample.data.remote.ChatSocketService
import com.example.composechatexample.data.remote.ChatSocketServiceImpl
import com.example.composechatexample.data.remote.MessageService
import com.example.composechatexample.data.remote.MessageServiceImpl
import com.example.composechatexample.data.remote.OnboardingService
import com.example.composechatexample.data.remote.OnboardingServiceImpl
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.data.remote.UserServiceImpl
import com.example.composechatexample.utils.Validator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("preferences_name", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideValidator(): Validator {
        return Validator()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient): MessageService {
        return MessageServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideChatSocketService(client: HttpClient): ChatSocketService {
        return ChatSocketServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideOnboardingService(client: HttpClient): OnboardingService {
        return OnboardingServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideUserService(client: HttpClient): UserService {
        return UserServiceImpl(client)
    }
}