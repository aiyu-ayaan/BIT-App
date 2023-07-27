package com.atech.login.ui.profile

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.atech.core.datastore.Cgpa
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.firebase.auth.UserData
import com.atech.core.firebase.auth.UserModel
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.fromJSON
import com.atech.login.databinding.FragmentProfileBinding
import com.atech.theme.getDate
import com.atech.theme.loadCircular
import com.atech.theme.navigateWithInAppDeepLink
import com.atech.theme.toast
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : DialogFragment() {
    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var authCases: AuthUseCases

    @Inject
    lateinit var fireStoreCases: FirebaseCases

    @Inject
    lateinit var pref: SharedPreferences

    private val client: SignInClient by lazy { Identity.getSignInClient(requireContext()) }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        setUiData()
        binding.apply {
            signOut()
            deleteAccount()
        }

        return MaterialAlertDialogBuilder(
            requireContext()
        ).setView(binding.root).create().also { alertDialog ->
            val window = alertDialog.window
            val wlp: WindowManager.LayoutParams = window?.attributes!!
            wlp.gravity = Gravity.TOP
            window.attributes = wlp
        }
    }

    private fun setUiData() {
        authCases.userDataFromDb.invoke { (user, error) ->
            if (error != null) {
                toast(error.message.toString())
                Log.e(TAGS.BIT_ERROR.name, "onCreateDialog: $error")
                return@invoke
            }
            user?.let { userModel ->
                Handler(Looper.getMainLooper()).also {
                    it.post {
                        binding.setUserDetails(userModel)
                    }
                }
            }
        }
        fireStoreCases.getUserSaveDetails.invoke(
            authCases.getUid.invoke()!!
        ) { (user, error) ->
            if (error != null) {
                toast(error.message.toString())
                Log.e(TAGS.BIT_ERROR.name, "onCreateDialog: $error")
                return@invoke
            }
            user?.let { userModel ->
                Handler(Looper.getMainLooper()).also {
                    it.post {
                        binding.setUserData(userModel)
                    }
                }
            }
        }
    }

    private fun FragmentProfileBinding.setUserDetails(userModel: UserModel) = this.apply {
        textViewName.text = userModel.name
        textViewEmail.text = userModel.email
        imageViewProfile.loadCircular(userModel.profilePic)
        textViewJoined.text = resources.getString(
            com.atech.theme.R.string.last_sync, userModel.syncTime?.getDate()
        )
        icClose.setOnClickListener {
            dismiss()
        }
    }

    private fun FragmentProfileBinding.setUserData(data: UserData) = this.apply {
        outlinedTextFieldUserCourse.editText?.setText(data.course)
        outlinedTextFieldCurrentSem.editText?.setText(data.sem)
        data.cgpa?.let { cgpa1 ->
            fromJSON(cgpa1, Cgpa::class.java)?.let {
                binding.outlinedTextFieldUserCgpa.editText?.setText(
                    String.format(
                        "%.2f", it.cgpa
                    )
                )
            }
        }
    }

    private fun FragmentProfileBinding.signOut() = this.signOut.setOnClickListener {
        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(com.atech.theme.R.string.sign_out))
            .setMessage(resources.getString(com.atech.theme.R.string.sign_out_message))
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                authCases.logout.invoke()
                client.signOut()
                pref.edit().apply {
                    putBoolean(SharePrefKeys.PermanentSkipLogin.name, false)
                }.apply()
                navigateToLogin()
                dismiss()
            }.setNegativeButton("no") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun FragmentProfileBinding.deleteAccount() =
        this.textViewDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    client.signOut()
                    authCases.deleteUser.invoke {
                        if (it != null) {
                            toast("Something went wrong, Please try again later.")
                            Log.e(TAGS.BIT_ERROR.name, "onCreateDialog: $it")
                            return@invoke
                        }
                        navigateToLogin()
                    }
                }.setNegativeButton("no", null).show()
        }

    private fun navigateToLogin() {
        navigateWithInAppDeepLink(
            BASE_IN_APP_NAVIGATION_LINK + Destination.LogIn.value
        )
    }
}