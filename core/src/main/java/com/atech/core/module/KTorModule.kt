package com.atech.core.module

import android.content.Context
import com.atech.core.data_source.ktor.BitAppApiImp
import com.atech.core.data_source.ktor.BitAppApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import kotlinx.serialization.json.Json
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KTorModule {

    @Provides
    @Singleton
    fun provideClient(
        @ApplicationContext context: Context
    ): HttpClient =
        HttpClient(OkHttp) {
            engine {
                config {
                    cache(Cache(File(context.cacheDir, "ktor"), 50 * 1024 * 1024))
                }
            }

//            if (BuildConfig.DEBUG)
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