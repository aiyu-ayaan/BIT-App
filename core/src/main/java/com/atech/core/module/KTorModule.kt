package com.atech.core.module

import com.atech.core.data_source.ktor.BitAppApiImp
import com.atech.core.data_source.ktor.BitAppApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.cache.HttpCache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KTorModule {

    @Provides
    @Singleton
    fun provideClient(): HttpClient =
        HttpClient(Android) {
            install(HttpCache)
            install(Logging) {
                level = LogLevel.ALL
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )

            }
        }

    @Provides
    @Singleton
    fun provideApiService(
        client: HttpClient
    ): BitAppApiService = BitAppApiImp(
        client = client
    )

}