/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.module

import android.content.Context
import com.atech.core.datasource.retrofit.BitAppApiService
import com.atech.core.datasource.retrofit.BitAppApiService.Companion.BASE_URL
import com.atech.core.datasource.retrofit.CollegeNoticeApiService
import com.atech.core.utils.cacheSize
import com.atech.core.utils.hasNetwork
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {
    @Provides
    @Singleton
    fun provideConvertor(): GsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder().setLenient().create()
    )


    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, cacheSize)

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context, cache: Cache): OkHttpClient =
        OkHttpClient.Builder().cache(cache).addNetworkInterceptor { chain ->
            var request = chain.request()
            request = if (!hasNetwork(context)!!) request.newBuilder().header(
                "Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
            ).build()
            else {
                val maxAge =
                    5 // read from cache for 60 seconds even if there is internet connection
                request.newBuilder().header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader("Pragma").build()
            }
            chain.proceed(request)
        }.build()

    @Provides
    @Singleton
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory, okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory).client(okHttpClient).build()


    @Provides
    @Singleton
    fun provideCollegeNoticeApiService(): CollegeNoticeApiService =
        Retrofit.Builder().baseUrl(CollegeNoticeApiService.COLLEGE_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(CollegeNoticeApiService::class.java)

    @Provides
    @Singleton
    fun provideSyllabusApi(retrofit: Retrofit): BitAppApiService =
        retrofit.create(BitAppApiService::class.java)
}