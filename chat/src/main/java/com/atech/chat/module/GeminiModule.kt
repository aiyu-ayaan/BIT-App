/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.chat.module

import com.atech.chat.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {
    @Provides
    @Singleton
    fun provideGemini(): GenerativeModel {
        val harassmentSafety =
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)

        val hateSpeechSafety =
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)
        val config = generationConfig {
            stopSequences = listOf(
                "fuck",
                "sex",
            )
            temperature = 0.9f
            topK = 16
            topP = 0.1f
        }
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey,
            generationConfig = config,
            safetySettings = listOf(
                harassmentSafety, hateSpeechSafety
            )
        )
    }
}