package com.atech.login.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.core.utils.SharePrefKeys
import com.atech.login.R
import com.atech.login.databinding.FragmentLoginBinding
import com.atech.login.utils.GoogleSignUIClient
import com.atech.theme.Axis
import com.atech.theme.ParentActivity
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.getLastDestination
import com.atech.theme.isDark
import com.atech.theme.launchWhenStarted
import com.atech.theme.navigate
import com.atech.theme.navigateWithInAppDeepLink
import com.atech.theme.toast
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.SignInButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding: FragmentLoginBinding by viewBinding()

    private val googleSignClient: GoogleSignUIClient by lazy {
        GoogleSignUIClient(
            requireActivity(), Identity.getSignInClient(requireActivity())
        )
    }
    private val mainActivity: ParentActivity by lazy {
        requireActivity() as ParentActivity
    }

    @Inject
    lateinit var authUseCases: AuthUseCases

    @Inject
    lateinit var pref: SharedPreferences


    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { intent ->
                    googleSignClient.getSignUserFromIntend(intent) { token, error ->
                        if (error != null) {
                            toast(error.toString())
                            return@getSignUserFromIntend
                        }
                        authUseCases.login.invoke(token) { (ui, exception) ->
                            if (exception != null) {
                                toast(exception.toString())
                                return@invoke
                            }
                            ui?.let { hasData ->
                                checkUserData(hasData)
                            }
                        }
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.Z)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenLogic()
        binding.apply {
            for1Time()
            signInButton()
            skipButton()
            whyLogin()
        }
    }

    private fun for1Time() {
        pref.getBoolean(SharePrefKeys.FirstTimeLogIn.name, true)
            .let { firstTime ->
                if (firstTime) {
                    authUseCases.hasLogIn.invoke().let {
                        if (it) {
                            authUseCases.logout.invoke()
                        }
                    }
                    pref.edit().apply {
                        putBoolean(SharePrefKeys.FirstTimeLogIn.name, false)
                    }.apply()
                }
            }
    }

    private fun screenLogic() {
        val isLogIn = authUseCases.hasLogIn.invoke()
        val hasDataInCloud = pref.getBoolean(SharePrefKeys.UserHasDataInCloud.name, false)
        val isRestoreDone = pref.getBoolean(SharePrefKeys.RestoreDone.name, false)
        val isPermanentSkipLogin = pref.getBoolean(SharePrefKeys.PermanentSkipLogin.name, false)
        val isSetUpDone = pref.getBoolean(SharePrefKeys.SetUpDone.name, false)
        val fromHome = getLastDestination() == mainActivity.getHomeFragmentId()
        if (!isLogIn && isPermanentSkipLogin && !fromHome) {
            if (isSetUpDone) navigateToHome()
            else navigateToSetup()
        }

        if (isLogIn && (hasDataInCloud || isRestoreDone || isSetUpDone)) {
            if (isRestoreDone || isSetUpDone) navigateToHome()
            else navigateToLoadingScreen()
        }
    }

    private fun navigateToHome() {
        findNavController().popBackStack()
        navigateWithInAppDeepLink(
            BASE_IN_APP_NAVIGATION_LINK + Destination.Home.value
        )
    }

    private fun FragmentLoginBinding.skipButton() = this.buttonSkip.apply {
        setOnClickListener {
            pref.edit().apply {
                putBoolean(SharePrefKeys.PermanentSkipLogin.name, true)
            }.apply()
            val isSetUpDone = pref.getBoolean(SharePrefKeys.SetUpDone.name, false)
            if (isSetUpDone) navigateToHome()
            else navigateToSetup()
        }
    }

    private fun checkUserData(hasData: Boolean) {
        if (hasData) navigateToLoadingScreen().also {
            pref.edit().apply {
                putBoolean(SharePrefKeys.UserHasDataInCloud.name, true)
            }.apply()
        }
        else navigateToSetup()
    }

    private fun navigateToLoadingScreen() {
        exitTransition(Axis.X)
        val action = LoginFragmentDirections.actionLoginFragmentToLoadingFragment()
        navigate(action)
    }

    private fun navigateToSetup() {
        exitTransition(Axis.X)
        val action = LoginFragmentDirections.actionLoginFragmentToSetupFragment()
        navigate(action)
    }

    private fun FragmentLoginBinding.whyLogin() = this.textViewWhyToLogIn.setOnClickListener {
        whyULogInDialog()
    }

    private fun whyULogInDialog() {
        val dialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle("Why do you need to log in?")
                .setMessage(
                    """
                        You need to log in to save your data (e.g. Attendance, GPA, Course preferences) in the cloud.
                        
                        This way you can access your data from any device.
                    """.trimIndent()
                ).setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    private fun FragmentLoginBinding.signInButton() = this.signInButton.apply {
        setSize(SignInButton.SIZE_WIDE)
        setColorScheme(if (activity?.isDark() == true) SignInButton.COLOR_DARK else SignInButton.COLOR_LIGHT)
        setOnClickListener {
            signIn()
        }
    }

    private fun signIn() = launchWhenStarted {
        googleSignClient.intentSender().let { (intentSender, error) ->
            if (error != null) {
                toast(error.toString())
                return@let
            }
            activityResult.launch(
                IntentSenderRequest.Builder(
                    intentSender ?: return@let
                ).build()
            )
        }
    }
}