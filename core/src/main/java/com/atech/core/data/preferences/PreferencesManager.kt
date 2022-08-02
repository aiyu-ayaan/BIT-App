/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/13/22, 9:22 PM
 */



package com.atech.core.data.preferences

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Keep
@Parcelize
data class Cgpa(
    var sem1: Double = 0.0,
    var sem2: Double = 0.0,
    var sem3: Double = 0.0,
    var sem4: Double = 0.0,
    var sem5: Double = 0.0,
    var sem6: Double = 0.0,
    val cgpa: Double = 1.0
) : Parcelable {
    @IgnoredOnParcel
    val isAllZero =
        sem1 == 0.0 && sem2 == 0.0 && sem3 == 0.0 && sem4 == 0.0 && sem5 == 0.0 && sem6 == 0.0
}

@Keep
@Parcelize
data class FilterPreferences(
    val defPercentage: Int,
    val course: String,
    val sem: String,
    val semSyllabus: String,
    val searchPreference: SearchPreference,
    val cgpa: Cgpa
) : Parcelable

@Keep
@Parcelize
data class SearchPreference(
    val event: Boolean,
    val holiday: Boolean,
    val notice: Boolean,
    val subject: Boolean,
) : Parcelable


@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext val context: Context) {
    private val TAG = "PreferencesManager"
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")


    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            when (exception) {
                is IOException -> {
                    emit(emptyPreferences())
                    Log.e(TAG, "$exception")
                }
                else -> throw exception
            }
        }
        .map { preferences ->
            val defPercentage = preferences[PreferencesKeys.DEFAULT_PERCENTAGE] ?: 75
            val course = preferences[PreferencesKeys.DEF_COURSE] ?: "BCA"
            val sem = preferences[PreferencesKeys.DEF_SEM] ?: "1"
            val semSyllabus = preferences[PreferencesKeys.DEF_SEM_SYLLABUS] ?: "1"

            val settingEvent = preferences[PreferencesKeys.DEF_SETTING_EVENT] ?: true
            val settingHoliday = preferences[PreferencesKeys.DEF_SETTING_HOLIDAY] ?: true
            val settingNotice = preferences[PreferencesKeys.DEF_SETTING_NOTICE] ?: true
            val settingSubject = preferences[PreferencesKeys.DEF_SETTING_SUBJECT] ?: true
            val sem1Cgpa = preferences[PreferencesKeys.DEF_SEM_1_CGPA] ?: 0.0
            val sem2Cgpa = preferences[PreferencesKeys.DEF_SEM_2_CGPA] ?: 0.0
            val sem3Cgpa = preferences[PreferencesKeys.DEF_SEM_3_CGPA] ?: 0.0
            val sem4Cgpa = preferences[PreferencesKeys.DEF_SEM_4_CGPA] ?: 0.0
            val sem5Cgpa = preferences[PreferencesKeys.DEF_SEM_5_CGPA] ?: 0.0
            val sem6Cgpa = preferences[PreferencesKeys.DEF_SEM_6_CGPA] ?: 0.0
            val cgpa = preferences[PreferencesKeys.DEF_CGPA] ?: 0.0



            FilterPreferences(
                defPercentage,
                course,
                sem,
                semSyllabus,
                SearchPreference(
                    settingEvent,
                    settingHoliday,
                    settingNotice,
                    settingSubject
                ),
                Cgpa(
                    sem1Cgpa,
                    sem2Cgpa,
                    sem3Cgpa,
                    sem4Cgpa,
                    sem5Cgpa,
                    sem6Cgpa,
                    cgpa
                )
            )
        }


    suspend fun updateCourse(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_COURSE] = value
        }
    }

    suspend fun updateCgpa(cgpa: Cgpa) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_SEM_1_CGPA] = cgpa.sem1
            preferences[PreferencesKeys.DEF_SEM_2_CGPA] = cgpa.sem2
            preferences[PreferencesKeys.DEF_SEM_3_CGPA] = cgpa.sem3
            preferences[PreferencesKeys.DEF_SEM_4_CGPA] = cgpa.sem4
            preferences[PreferencesKeys.DEF_SEM_5_CGPA] = cgpa.sem5
            preferences[PreferencesKeys.DEF_SEM_6_CGPA] = cgpa.sem6
            preferences[PreferencesKeys.DEF_CGPA] = cgpa.cgpa
        }
    }

    suspend fun updateSem(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_SEM] = value
        }
    }


    suspend fun updatePercentage(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PERCENTAGE] = value
        }
    }

    suspend fun updateSemSyllabus(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_SEM_SYLLABUS] = value
        }
    }

    suspend fun updateSearchSetting(
        event: Boolean,
        holiday: Boolean,
        notice: Boolean,
        subject: Boolean
    ) {
        context.dataStore.edit { preference ->
            preference[PreferencesKeys.DEF_SETTING_EVENT] = event
            preference[PreferencesKeys.DEF_SETTING_HOLIDAY] = holiday
            preference[PreferencesKeys.DEF_SETTING_NOTICE] = notice
            preference[PreferencesKeys.DEF_SETTING_SUBJECT] = subject
        }
    }


    private object PreferencesKeys {
        val DEF_COURSE = stringPreferencesKey("def_course")
        val DEF_SEM = stringPreferencesKey("def_sem")
        val DEFAULT_PERCENTAGE = intPreferencesKey("default_percentage")
        val DEF_SEM_SYLLABUS = stringPreferencesKey("default_sem_syllabus")
        val DEF_SETTING_EVENT = booleanPreferencesKey("default_setting_event")
        val DEF_SETTING_HOLIDAY = booleanPreferencesKey("default_setting_holiday")
        val DEF_SETTING_NOTICE = booleanPreferencesKey("default_setting_notice")
        val DEF_SETTING_SUBJECT = booleanPreferencesKey("default_setting_subject")
        val DEF_SEM_1_CGPA = doublePreferencesKey("default_sem_1_cgpa")
        val DEF_SEM_2_CGPA = doublePreferencesKey("default_sem_2_cgpa")
        val DEF_SEM_3_CGPA = doublePreferencesKey("default_sem_3_cgpa")
        val DEF_SEM_4_CGPA = doublePreferencesKey("default_sem_4_cgpa")
        val DEF_SEM_5_CGPA = doublePreferencesKey("default_sem_5_cgpa")
        val DEF_SEM_6_CGPA = doublePreferencesKey("default_sem_6_cgpa")
        val DEF_CGPA = doublePreferencesKey("default_cgpa")
    }
}