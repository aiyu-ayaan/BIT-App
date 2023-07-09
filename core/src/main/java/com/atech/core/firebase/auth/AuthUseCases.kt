package com.atech.core.firebase.auth

import android.content.Context
import android.net.Uri
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.utils.Encryption.encryptText
import com.atech.core.utils.Encryption.getCryptore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kazakago.cryptore.Cryptore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val login: Login,
    val hasLogIn: HasLogIn,
    val getUid: GetUid
)

class HasLogIn @Inject constructor(
    private val auth: FirebaseAuth
) {
    operator fun invoke(): Boolean = auth.currentUser != null
}

class GetUid @Inject constructor(
    private val auth: FirebaseAuth
) {
    operator fun invoke(): String? = auth.currentUser?.uid
}

class Login @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val firebaseCases: FirebaseCases
) {
    operator fun invoke(token: String, callback: (Pair<Boolean?, Exception?>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    val userId = user.uid
                    val userName = user.displayName
                    val userEmail = user.email
                    val userPhoto = user.photoUrl
                    val cryptore = context.getCryptore(userId)
                    encryptData(
                        cryptore, userId, userName, userEmail, userPhoto, callback
                    )
                }
            } else callback(null to task.exception)
        }
    }

    private fun encryptData(
        cryptore: Cryptore,
        userId: String,
        userName: String?,
        userEmail: String?,
        userPhoto: Uri?,
        callback: (Pair<Boolean?, Exception?>) -> Unit
    ) {
        try {
            val encryptedUserName = userName?.let { cryptore.encryptText(it) }
            val encryptedUserEmail = userEmail?.let { cryptore.encryptText(it) }
            val encryptedUserPhoto = userPhoto?.let { cryptore.encryptText(it.toString()) }
            val userModel = UserModel(
                userId, encryptedUserName, encryptedUserEmail, encryptedUserPhoto
            )
            addUserToDatabase(userModel, callback)
        } catch (e: Exception) {
            callback(null to e)
        }
    }

    private fun addUserToDatabase(
        userModel: UserModel, callback: (Pair<Boolean?, Exception?>) -> Unit
    ) {
        firebaseCases.addUser.invoke(userModel) { (uid, error) ->
            if (error != null) callback(null to error)
            else firebaseCases.checkUserData.invoke(uid) { (hasData, exception) ->
                if (exception != null) callback(null to exception)
                else callback(hasData to null)
            }
        }
    }
}