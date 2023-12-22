package com.atech.core.usecase

import android.content.Context
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.datasource.room.attendance.AttendanceDao
import com.atech.core.datasource.room.syllabus.SyllabusDao
import com.atech.core.usecase.Encryption.encryptText
import com.atech.core.usecase.Encryption.getCryptore
import com.atech.core.utils.BitAppScope
import com.atech.core.utils.UpdateDataType
import com.atech.core.utils.fromJSON
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class AuthUseCases @Inject constructor(
    val logIn: LogIn, val getUid: GetUid,
    val hasLogIn: HasLogIn,
    val uploadData: UploadData,
    val performRestore: PerformRestore
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
                if (exception != null) Pair(false, exception)
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


data class HasLogIn @Inject constructor(
    private val auth: FirebaseAuth
) {
    operator fun invoke(): Boolean = auth.currentUser != null
}


data class GetUid @Inject constructor(
    private val auth: FirebaseAuth
) {
    operator fun invoke(): String? = auth.currentUser?.uid
}

data class UploadData @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseLoginUseCase,
) {
    suspend fun invoke(
        updateDataType: UpdateDataType
    ): Exception? = try {
        when (updateDataType) {
            is UpdateDataType.UpdateCgpa -> firebaseCases.uploadDataToFirebase.updateCgpa(
                auth.currentUser?.uid.toString(), updateDataType.cgpa
            )

            is UpdateDataType.UpdateCourseSem -> firebaseCases.uploadDataToFirebase.updateCourse(
                auth.currentUser?.uid.toString(), updateDataType.course, updateDataType.sem
            )

            is UpdateDataType.UploadAttendance -> firebaseCases.uploadDataToFirebase.updateAttendance(
                auth.currentUser?.uid.toString(), updateDataType.data
            )
        }
    } catch (e: Exception) {
        e
    }
}

data class PerformRestore @Inject constructor(
    private val firebaseCases: FirebaseLoginUseCase,
    private val dataStoreCases: DataStoreCases,
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao,
) {
    suspend operator fun invoke(
        uid: String,
        onCompletion: () -> Unit
    ): Exception? = try {
        val (user, exception) = firebaseCases.getUserSavedData(uid)
        exception.also { onCompletion.invoke() } ?: if (user == null) {
            Exception("User not found")
        } else {
            user.course.let { dataStoreCases.updateCourse(it) }
            user.sem.let { dataStoreCases.updateSem(it) }
            user.cgpa?.let { cgpa ->
                fromJSON(cgpa, Cgpa::class.java)?.let {
                    dataStoreCases.updateCgpa(it)
                }
            }
            onCompletion.invoke()
            null
        }
    } catch (e: Exception) {
        e
    }
}