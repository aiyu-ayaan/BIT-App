package com.atech.bit

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.atech.core.utils.AppTheme
import com.atech.core.utils.SharePrefKeys
import com.atech.theme.setAppTheme
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BITApp : Application() {

    @Inject
    lateinit var pref: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        when (pref.getString(SharePrefKeys.AppTheme.name, AppTheme.Sys.name)) {
            AppTheme.Sys.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppTheme.Light.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_NO)
            AppTheme.Dark.name -> setAppTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}