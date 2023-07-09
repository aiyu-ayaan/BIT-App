package com.atech.login.ui

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import com.atech.login.R
import com.atech.login.databinding.FragmentLoginBinding
import com.atech.theme.Axis
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.isDark
import com.atech.theme.navigate
import com.google.android.gms.common.SignInButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding: FragmentLoginBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signInButton()
            skipButton()
            whyLogin()
        }
    }

    private fun FragmentLoginBinding.skipButton() = this.buttonSkip.apply {
        setOnClickListener {
            navigateToSetup()
        }
    }

    private fun FragmentLoginBinding.signInButton() = this.signInButton.apply {
        setSize(SignInButton.SIZE_WIDE)
        setColorScheme(if (activity?.isDark() == true) SignInButton.COLOR_DARK else SignInButton.COLOR_LIGHT)
        setOnClickListener {
//            signIn() TODO: Implement this
        }
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
        binding.textViewWhyToLogIn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Why do you need to log in?")
                .setMessage(
                    """
                        You need to log in to save your data (e.g. Attendance, GPA, Course preferences) in the cloud.
                        
                        This way you can access your data from any device.
                    """.trimIndent()
                )
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }
}