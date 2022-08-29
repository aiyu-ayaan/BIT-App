package com.atech.bit.ui.fragments.login

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentLoginBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.bit.utils.Encryption.encryptText
import com.atech.bit.utils.Encryption.getCryptore
import com.atech.core.data.network.user.UserModel
import com.atech.core.utils.KEY_USER_DONE_SET_UP
import com.atech.core.utils.KEY_USER_HAS_DATA_IN_DB
import com.atech.core.utils.isDark
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kazakago.cryptore.Cryptore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogInFragment : Fragment(R.layout.fragment_login) {

    private val TAG = LogInFragment::class.java.simpleName

    private val binding: FragmentLoginBinding by viewBinding()
    private val viewModel: UserDataViewModel by activityViewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var pref: SharedPreferences

    private var cryptore: Cryptore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account!!.id)
                    account.idToken?.let { token ->
                        firebaseAuthWithGoogle(token)
                    }

                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            binding.signInButton.apply {
                setSize(SignInButton.SIZE_WIDE)
                setColorScheme(if (activity?.isDark() == true) SignInButton.COLOR_DARK else SignInButton.COLOR_LIGHT)
                setOnClickListener {
                    signIn()
                }
            }
        }
        if (auth.currentUser != null) {
            val setUp = pref.getBoolean(KEY_USER_DONE_SET_UP, false)
            if (setUp)
                findNavController().navigate(
                    LogInFragmentDirections.actionLogInFragmentToHomeFragment()
                )
            else {
                val hasData = pref.getBoolean(KEY_USER_HAS_DATA_IN_DB, false)
                setDestination(hasData)
            }
        }
    }


    private fun firebaseAuthWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = user.uid
                        val userName = user.displayName
                        val userEmail = user.email
                        val userPhoto = user.photoUrl
                        encryptData(userId, userName, userEmail, userPhoto)
                    }
                } else {
                    Toast.makeText(requireContext(), "${task.exception}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "firebaseAuthWithGoogle Exception: ${task.exception}")
                }
            }
    }

    private fun encryptData(
        userId: String,
        userName: String?,
        userEmail: String?,
        userPhoto: Uri?
    ) = lifecycleScope.launchWhenStarted {
        try {
            cryptore = context?.getCryptore(userId)
            val encryptedUserName = userName?.let { cryptore?.encryptText(it) }
            val encryptedUserEmail = userEmail?.let { cryptore?.encryptText(it) }
            val encryptedUserPhoto = userPhoto?.let { cryptore?.encryptText(it.toString()) }
            val userModel = UserModel(
                userId,
                encryptedUserName,
                encryptedUserEmail,
                encryptedUserPhoto
            )
            addUserToDatabase(userModel)
        } catch (e: Exception) {
            Log.e(TAG, "encryptData: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Something went wrong. Try again later",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addUserToDatabase(userModel: UserModel) = lifecycleScope.launchWhenStarted {
        viewModel.addUser(
            userModel,
            { uid ->
                checkHasData(uid)
            },
        ) { exception ->
            Toast.makeText(requireContext(), "$exception", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkHasData(uid: String) {
        viewModel.checkUserData(uid, {
            pref.edit()
                .putBoolean(KEY_USER_HAS_DATA_IN_DB, it)
                .apply()
            setDestination(it)
        }, {
            Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setDestination(it: Boolean) {
        if (it) {
            navigateToLoading()
        } else {
            navigateToSetup()
        }
    }

    private fun navigateToLoading() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        findNavController().navigate(
            NavGraphDirections.actionGlobalLoadingDataFragment(auth.currentUser!!.uid)
        )
    }

    private fun navigateToSetup() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        val action =
            NavGraphDirections.actionGlobalStartUpFragment()
        findNavController().navigate(
            action
        )
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activityResult.launch(
            Intent(
                signInIntent,
            )
        )
    }
}