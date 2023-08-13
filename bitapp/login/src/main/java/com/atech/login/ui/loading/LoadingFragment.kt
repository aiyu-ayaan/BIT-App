package com.atech.login.ui.loading

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.login.R
import com.atech.theme.Axis
import com.atech.theme.enterTransition
import com.atech.theme.navigateWithInAppDeepLink
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoadingFragment : Fragment(R.layout.fragment_loading) {


    @Inject
    lateinit var authCases: AuthUseCases

    @Inject
    lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref.edit().apply {
            putBoolean(SharePrefKeys.SetUpDone.name, false)
            putBoolean(SharePrefKeys.PermanentSkipLogin.name, false)
            putBoolean(SharePrefKeys.RestoreDone.name, false)
        }.apply()
        performRestore()
    }

    private fun performRestore() {
        authCases.performRestore.invoke { error ->
            if (error != null) setDestination(false).also {
                Log.e(TAGS.BIT_ERROR.name, "performRestore: $error")
                toast("performRestore error : $error")
                return@invoke
            }
            setDestination(true)
        }
    }

    private fun setDestination(isDone: Boolean) {
        pref.edit().apply {
            putBoolean(SharePrefKeys.RestoreDone.name, true)
            putBoolean(SharePrefKeys.SetUpDone.name, true)
        }.apply()
        navigateWithInAppDeepLink(
            BASE_IN_APP_NAVIGATION_LINK + if (isDone) Destination.Home.value else Destination.ChooseSem.value
        )
    }
}