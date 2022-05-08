/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/13/22, 9:22 PM
 */



package com.aatec.core.data.preferences

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aatec.core.utils.Gender
import com.aatec.core.utils.Section
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Keep
@Parcelize
data class FilterPreferences(
    val defPercentage: Int,
    val course: String,
    val sem: String,
    val semSyllabus: String,
    val searchPreference: SearchPreference,
    val timeTablePreferences: TimeTablePreferences,
    val firstOpenTimeTable: Boolean
) : Parcelable

@Keep
@Parcelize
data class SearchPreference(
    val event: Boolean,
    val holiday: Boolean,
    val notice: Boolean,
    val subject: Boolean,
) : Parcelable

@Keep
@Parcelize
data class TimeTablePreferences(
    val course: String,
    val gender: Gender,
    val sem: String,
    val section: Section
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
            val timeTableGender =
                Gender.valueOf(
                    preferences[PreferencesKeys.DEF_TIME_TABLE_GENDER] ?: Gender.Boys.name
                )

            val section = Section.valueOf(
                preferences[PreferencesKeys.DEF_TIME_TABLE_SECTION] ?: Section.A.name
            )
            val firstTimeTableOpen = preferences[PreferencesKeys.DEF_FIRST_TIME_TABLE_OPEN] ?: true


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
                TimeTablePreferences(course, timeTableGender, sem, section),
                firstTimeTableOpen
            )
        }


    suspend fun updateCourse(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_COURSE] = value
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

    suspend fun updateFirstTimeTableOpen(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_FIRST_TIME_TABLE_OPEN] = value
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

    suspend fun updateTimeTableSettings(
        course: String,
        gender: Gender,
        sem: String,
        sec: Section
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEF_COURSE] = course
            preferences[PreferencesKeys.DEF_TIME_TABLE_GENDER] = gender.name
            preferences[PreferencesKeys.DEF_SEM] = sem
            preferences[PreferencesKeys.DEF_TIME_TABLE_SECTION] = sec.name
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
        val DEF_TIME_TABLE_GENDER = stringPreferencesKey("def_time_table_gender")
        val DEF_TIME_TABLE_SECTION = stringPreferencesKey("def_time_table_section")
        val DEF_FIRST_TIME_TABLE_OPEN = booleanPreferencesKey("def_first_time_table_open")
    }
}