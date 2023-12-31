package com.atech.core.usecase

import android.content.Context
import android.util.Log
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.auth.AttendanceUploadModel
import com.atech.core.datasource.firebase.auth.UserData
import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.datasource.firebase.auth.toAttendanceModelList
import com.atech.core.datasource.room.attendance.AttendanceDao
import com.atech.core.datasource.room.syllabus.SyllabusDao
import com.atech.core.utils.BitAppScope
import com.atech.core.utils.Encryption.decryptText
import com.atech.core.utils.Encryption.encryptText
import com.atech.core.utils.Encryption.getCryptore
import com.atech.core.utils.TAGS
import com.atech.core.utils.UpdateDataType
import com.atech.core.utils.fromJSON
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class AuthUseCases @Inject constructor(
    val logIn: LogIn,
    val getUid: GetUid,
    val hasLogIn: HasLogIn,
    val uploadData: UploadData,
    val performRestore: PerformRestore,
    val getUserDataFromAuth: GetUserDataFromAuth,
    val getUserSavedData: GetUserDetails,
    val getUserFromDatabase: GetUserFromDatabase,
    val signOut: SignOut,
    val deleteUser: DeleteUser,
    val chats: Chats,
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
                Log.d(TAGS.BIT_DEBUG.name, "LoginScreen: $uid")
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
        uid: String, onCompletion: () -> Unit
    ): Exception? = try {
        val (user, exception) = firebaseCases.getUserSaveDetails(uid)
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
            user.attendance?.let {
                fromJSON(
                    it, Array<AttendanceUploadModel>::class.java
                )?.let { attendanceData ->
                    val backupAtt = attendanceData.toAttendanceModelList()
                    attendanceDao.insertAll(backupAtt)
                    backupAtt.filter { it1 -> it1.fromSyllabus == true }
                        .forEach { attendanceModel ->
                            syllabusDao.getSyllabus(attendanceModel.subject)?.let { syllabus ->
                                syllabusDao.updateSyllabus(
                                    syllabus.copy(
                                        isAdded = true
                                    )
                                )
                            }
                        }
                }
            }
            onCompletion.invoke()
            null
        }
    } catch (e: Exception) {
        e
    }
}

data class GetUserDataFromAuth @Inject constructor(
    private val auth: FirebaseAuth,
) {
    operator fun invoke(): Pair<UserModel?, Exception?> = try {
        val user = auth.currentUser
        if (user == null) {
            Pair(null, Exception("User not found"))
        }
        user?.let { logInUser ->
            val userId = logInUser.uid
            val userName = logInUser.displayName
            val userEmail = logInUser.email
            val userPhoto = logInUser.photoUrl
            val userModel = UserModel(
                userId,
                userName,
                userEmail,
                userPhoto.toString(),
            )
            Pair(userModel, null)
        } ?: Pair(null, Exception("User not found"))
    } catch (e: Exception) {
        Pair(null, e)
    }
}


data class GetUserFromDatabase @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseLoginUseCase,
    @ApplicationContext private val context: Context,
) {
    suspend operator fun invoke(): Pair<UserModel?, Exception?> = try {
        firebaseCases.getUserEncryptedData.invoke(auth.uid!!).let { (user, ex) ->
            user?.let { enUser ->
                convertEncryptedData(auth.uid!!, enUser) to null
            } ?: run {
                null to ex
            }
        }
    } catch (e: Exception) {
        null to e
    }

    private fun convertEncryptedData(uid: String, user: UserModel): UserModel {
        val cryptore = context.getCryptore(uid)
        val email = cryptore.decryptText(user.email)
        val name = cryptore.decryptText(user.name)
        val profilePic = cryptore.decryptText(user.profilePic)
        return UserModel(
            email = email,
            name = name,
            profilePic = profilePic,
            uid = user.uid,
            syncTime = user.syncTime
        )
    }
}

data class GetUserDetails @Inject constructor(
    private val auth: FirebaseAuth, private val case: FirebaseLoginUseCase
) {
    suspend operator fun invoke(): Pair<UserData?, Exception?> = try {
        case.getUserSaveDetails.invoke(auth.currentUser!!.uid)
    } catch (e: Exception) {
        null to e
    }
}

data class SignOut @Inject constructor(
    private val auth: FirebaseAuth
) {
    operator fun invoke(
        action: () -> Unit
    ) {
        auth.signOut()
        action.invoke()
    }
}

data class DeleteUser @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseLoginUseCase,
) {
    suspend operator fun invoke(): Exception? = try {
        firebaseCases.deleteUser(
            auth.currentUser!!.uid
        )
    } catch (e: Exception) {
        e
    }
}

data class Chats @Inject constructor(
    private val auth: FirebaseAuth,
    private val getChatSettings: GetChatSettings,
    private val setChatSettings: SetChatSettings,
    private val hasUnlimitedAccess: CheckHasUnlimitedAccess
) {
    suspend fun getChatSettings(
    ): Pair<Triple<Boolean, Long, Int>?, Exception?> = try {
        getChatSettings.invoke(auth.currentUser!!.uid)
    } catch (e: Exception) {
        null to e
    }

    suspend fun updateChatEnable(
        isChatEnable: Boolean,
    ): Exception? = try {
        setChatSettings.updateChatEnable(
            auth.currentUser!!.uid, isChatEnable
        )
    } catch (e: Exception) {
        e
    }

    suspend fun updateLastChat(): Exception? = try {
        setChatSettings.updateLastChat(
            auth.currentUser!!.uid
        )
    } catch (e: Exception) {
        e
    }

    suspend fun updateCurrentChatNumber(
        currentChatNumber: Int,
    ): Exception? = try {
        setChatSettings.updateCurrentChatNumber(
            auth.currentUser!!.uid, currentChatNumber
        )
    } catch (e: Exception) {
        e
    }

    suspend fun checkUnlimitedAccess(
    ): Boolean = try {
        hasUnlimitedAccess.invoke(
            auth.currentUser!!.uid
        )
    } catch (e: Exception) {
        false
    }
}