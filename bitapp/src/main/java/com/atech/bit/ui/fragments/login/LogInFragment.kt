package com.atech.bit.ui.fragments.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.atech.bit.R
import com.atech.bit.databinding.FragmentLoginBinding
import com.atech.bit.utils.Encryption.encryptText
import com.atech.bit.utils.Encryption.getCryptore
import com.atech.core.data.network.user.UserModel
import com.atech.core.utils.isDark
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
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
    private val viewModel: LogInViewModel by viewModels()

    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private var cryptore: Cryptore? = null

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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.apply {
            binding.signInButton.apply {
                setSize(SignInButton.SIZE_WIDE)
                setColorScheme(if (activity?.isDark() == true) SignInButton.COLOR_DARK else SignInButton.COLOR_LIGHT)
                setOnClickListener {
                    signIn()
                }
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
            {
//                TODO : Check data is present or not
                Toast.makeText(requireContext(), "Succress", Toast.LENGTH_SHORT).show()
            },
        ) { exception ->
            Toast.makeText(requireContext(), "$exception", Toast.LENGTH_SHORT).show()
        }
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