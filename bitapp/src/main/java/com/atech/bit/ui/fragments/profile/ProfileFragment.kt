package com.atech.bit.ui.fragments.profile

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.FragmentProfileBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.bit.utils.calculateTimeDifference
import com.atech.core.utils.KEY_IS_USER_LOG_IN
import com.atech.core.utils.KEY_USER_DONE_SET_UP
import com.atech.core.utils.TAG
import com.atech.core.utils.loadImageCircular
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : DialogFragment() {

    private lateinit var binding: FragmentProfileBinding
    private val args: ProfileFragmentArgs by navArgs()
    private val userDataViewModel by viewModels<UserDataViewModel>()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.apply {
            textViewName.text = args.user.name
            textViewEmail.text = args.user.email
            args.user.profilePic?.loadImageCircular(
                binding.imageViewProfile,
            )
            icClose.setOnClickListener {
                dismiss()
            }

            binding.textViewJoined.text = resources.getString(
                R.string.last_sync,
                args.user.syncTime?.calculateTimeDifference()
            )
            userDataViewModel.getCourseSem(args.uid, { details ->
                details.split(" ").let {
                    binding.outlinedTextFieldUserCourse.editText?.setText(it[0])
                    binding.outlinedTextFieldCurrentSem.editText?.setText(it[1])
                }
            }, {
                Log.d(TAG, "onCreateDialog: $it")
            })
            userDataViewModel.getCGPA(args.uid, {
                binding.outlinedTextFieldUserCgpa.editText?.setText(
                    String.format(
                        "%.2f",
                        it.cgpa
                    )
                )
            }, {
                binding.outlinedTextFieldUserCgpa.editText?.setText("0.0")
            })
            signOut.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.sign_out))
                    .setMessage(resources.getString(R.string.sign_out_message))
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        auth.signOut()
                        googleSignInClient.signOut()
                        pref.edit().putBoolean(
                            KEY_USER_DONE_SET_UP,
                            false
                        ).apply()
                        updateIsLogIn()
                        dismiss()
                    }
                    .setNegativeButton("no") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            textViewDeleteAccount.setOnClickListener {
                deleteAccount()
            }
        }
        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        )
            .setView(binding.root)
        val alertDialog = dialog.create()
        val window = alertDialog.window
        val wlp: WindowManager.LayoutParams = window?.attributes!!
        wlp.gravity = Gravity.TOP
        window.attributes = wlp
        return alertDialog
    }

    private fun deleteAccount() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Yes") { dialog, _ ->
                userDataViewModel.deleteUser(args.uid, {
                    auth.currentUser?.delete()?.addOnSuccessListener {
                        googleSignInClient.signOut()
                        dialog.dismiss().also {
                            dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Your account is deleted successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateIsLogIn()
                        }
                    }
                }) {
                    Toast.makeText(
                        requireContext(),
                        "Can't proceed this progress right now. Try again later ",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("XXX", "deleteAccount: $it")
                }
            }
            .setNegativeButton("no") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateIsLogIn() {
        pref.edit().putBoolean(KEY_IS_USER_LOG_IN, false).apply()
    }
}