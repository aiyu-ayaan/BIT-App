package com.atech.core.usecase

import android.content.Context
import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.datasource.room.attendance.AttendanceDao
import com.atech.core.usecase.Encryption.encryptText
import com.atech.core.usecase.Encryption.getCryptore
import com.atech.core.utils.BitAppScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class AuthUseCases @Inject constructor(
    val logIn: LogIn,
)

data class LogIn @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val firebaseCases: FirebaseLoginUseCase,
    private val attendanceDao: AttendanceDao,
    private val dataStoreCases: DataStoreCases,
    @BitAppScope private val coroutineScope: CoroutineScope
) {
    suspend operator fun invoke(
        token: String
    ): Pair<Boolean, Exception?> = try {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val task = auth.signInWithCredential(credential).await()
        val user = task.user
        if (user == null) {
            Pair(false, Exception("User not found"))
        }
        user?.let { logInUser ->
            val userId = logInUser.uid
            val userName = logInUser.displayName
            val userEmail = logInUser.email
            val userPhoto = logInUser.photoUrl
            val cryptore = context.getCryptore(userId)

            val encryptedUserName = userName?.let { cryptore.encryptText(it) }
            val encryptedUserEmail = userEmail?.let { cryptore.encryptText(it) }
            val encryptedUserPhoto = userPhoto?.let { cryptore.encryptText(it.toString()) }
            val userModel = UserModel(
                userId, encryptedUserName, encryptedUserEmail, encryptedUserPhoto
            )
            firebaseCases.addUser(userModel).let { (uid, exception) ->
                dataStoreCases.clearAll.invoke()
                attendanceDao.deleteALl()
                if (exception != null)
                    Pair(false, exception)
                else firebaseCases.checkUserData(uid).let { (hasData, ex) ->
                    if (ex != null) Pair(false, ex)
                    else if (hasData == true) Pair(true, null)
                    else return Pair(false, null)
                }
            }
        } ?: Pair(false, Exception("User not found"))
    } catch (e: Exception) {
        Pair(false, e)
    }
}

class GetUid @Inject constructor(
    private val auth: FirebaseAuth
) {
    operator fun invoke(): String? = auth.currentUser?.uid
}
