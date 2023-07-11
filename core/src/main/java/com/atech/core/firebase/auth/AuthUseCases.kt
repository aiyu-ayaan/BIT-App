package com.atech.core.firebase.auth

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.atech.core.datastore.Cgpa
import com.atech.core.datastore.DataStoreCases
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.attendance.Days
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.BitAppScope
import com.atech.core.utils.Encryption.decryptText
import com.atech.core.utils.Encryption.encryptText
import com.atech.core.utils.Encryption.getCryptore
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kazakago.cryptore.Cryptore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val login: Login,
    val hasLogIn: HasLogIn,
    val getUid: GetUid,
    val performRestore: PerformRestore,
    val userData: GetUserData,
    val userDataFromDb: GetUserDataFromData,
    val uploadData: UploadData,
    val logout: Logout,
    val deleteUser: DeleteUser
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
    private val firebaseCases: FirebaseCases,
    private val attendanceDao: AttendanceDao,
    private val dataStoreCases: DataStoreCases,
    @BitAppScope private val coroutineScope: CoroutineScope
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
            coroutineScope.launch {
                dataStoreCases.clearAll.invoke()
                attendanceDao.deleteAll()
            }
            if (error != null) callback(null to error)
            else firebaseCases.checkUserData.invoke(uid) { (hasData, exception) ->
                if (exception != null) callback(null to exception)
                else callback(hasData to null)
            }
        }
    }
}

class GetUserData @Inject constructor(
    private val auth: FirebaseAuth,
) {
    operator fun invoke(callback: (Pair<UserModel?, Exception?>) -> Unit) {
        if (auth.currentUser == null) callback(null to Exception("User not logged in"))
        else auth.currentUser?.let { user ->
            val userId = user.uid
            val userName = user.displayName
            val userEmail = user.email
            val userPhoto = user.photoUrl
            val userModel = UserModel(userId, userName, userEmail, userPhoto.toString())
            callback(userModel to null)
        }
    }
}

class PerformRestore @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseCases,
    private val dataStoreCases: DataStoreCases,
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao,
    @BitAppScope private val scope: CoroutineScope
) {
    operator fun invoke(
        callback: (Exception?) -> Unit
    ) {
        if (auth.currentUser == null) return
        firebaseCases.getUserSaveDetails(
            auth.currentUser!!.uid
        ) { (data, exception) ->
            if (exception != null) {
                callback(exception)
                return@getUserSaveDetails
            }
            try {
                data?.let { userData ->
                    scope.launch(Dispatchers.IO) {
                        userData.course.let { dataStoreCases.updateCourse(it) }
                        userData.sem.let { dataStoreCases.updateSem(it) }
                        userData.cgpa?.let { cgpa ->
                            fromJSON(cgpa, Cgpa::class.java)?.let {
                                dataStoreCases.updateCgpa(it)
                            }
                        }
                        userData.attendance?.let {
                            fromJSON(
                                it, Array<AttendanceUploadModel>::class.java
                            )?.let { attendanceData ->
                                addAttendanceToDatabase(
                                    attendanceDao,
                                    syllabusDao,
                                    attendanceData.toList(),
                                )
                            }
                        }
                    }
                }
                callback(null)
            } catch (e: Exception) {
                callback(e)
            }
        }
    }

    private suspend fun addAttendanceToDatabase(
        attendanceDao: AttendanceDao,
        syllabusDao: SyllabusDao,
        attendanceUploadModel: List<AttendanceUploadModel>,
    ) {
        withContext(Dispatchers.IO) {
            val list = attendanceUploadModel.map {
                AttendanceModel(
                    subject = it.subject,
                    total = it.total,
                    present = it.present,
                    teacher = it.teacher,
                    fromSyllabus = it.fromSyllabus,
                    isArchive = it.isArchive,
                    created = it.created,
                    days = Days(
                        presetDays = arrayListOf(),
                        absentDays = arrayListOf(),
                        totalDays = arrayListOf()
                    ),
                    fromOnlineSyllabus = it.isFromOnlineSyllabus ?: false
                )
            }
            list.forEach {
                if (it.fromSyllabus == true) {
                    syllabusDao.getSyllabus(it.subject)?.let { syllabus ->
                        syllabusDao.updateSyllabus(syllabus.copy(isAdded = true))
                    }
                }
            }
            attendanceDao.insertAll(list)
        }
    }
}

class GetUserDataFromData @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseCases,
    @ApplicationContext private val context: Context,
    @BitAppScope private val scope: CoroutineScope
) {
    operator fun invoke(
        callback: (Pair<UserModel?, Exception?>) -> Unit
    ) {
        if (auth.currentUser == null) return
        firebaseCases.getUserDataFromDb.invoke(
            auth.currentUser!!.uid
        ) { (encryptedData, error) ->
            if (error != null) {
                callback(null to error)
            } else {
                try {
                    scope.launch(Dispatchers.IO) {
                        encryptedData?.let { user ->
                            val decryptedData = convertEncryptedData(
                                auth.currentUser!!.uid, user
                            ).await()
                            callback(decryptedData to null)
                        }
                    }
                } catch (e: Exception) {
                    callback(null to e)
                }
            }
        }
    }

    private fun convertEncryptedData(uid: String, user: UserModel): Deferred<UserModel?> =
        scope.async(Dispatchers.IO) {
            val cryptore = context.getCryptore(uid)
            val email = cryptore.decryptText(user.email)
            val name = cryptore.decryptText(user.name)
            val profilePic = cryptore.decryptText(user.profilePic)
            UserModel(
                email = email,
                name = name,
                profilePic = profilePic,
                uid = user.uid,
                syncTime = user.syncTime
            )
        }
}

class Logout @Inject constructor(
    private val auth: FirebaseAuth,
) {
    operator fun invoke(
        onComplete: () -> Unit = {}
    ) {
        auth.signOut()
        onComplete()
    }
}

sealed class UpdateDataType {
    data class Attendance(val data: List<AttendanceUploadModel>) : UpdateDataType()
    data class CourseSem(val course: String, val sem: String) : UpdateDataType()
    data class Cgpa(val cgpa: com.atech.core.datastore.Cgpa) : UpdateDataType()
}

class UploadData @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseCases,
) {
    operator fun invoke(
        updateDataType: UpdateDataType, callback: (Exception?) -> Unit
    ) {
        if (auth.currentUser == null) return
        when (updateDataType) {
            is UpdateDataType.Attendance -> firebaseCases.uploadData.updateAttendance(
                auth.currentUser!!.uid, updateDataType.data, callback
            )

            is UpdateDataType.Cgpa -> firebaseCases.uploadData.updateCGPA(
                auth.currentUser!!.uid, updateDataType.cgpa, callback
            )

            is UpdateDataType.CourseSem -> firebaseCases.uploadData.updateCourse(
                auth.currentUser!!.uid, updateDataType.course, updateDataType.sem, callback
            )
        }
    }
}

class DeleteUser @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseCases: FirebaseCases,
    private val pref: SharedPreferences,
) {
    operator fun invoke(
        callback: (Exception?) -> Unit
    ) {
        if (auth.currentUser == null) return
        firebaseCases.deleteUser.invoke(
            auth.currentUser!!.uid,
        ) {
            auth.currentUser?.delete()?.addOnSuccessListener {
                pref.edit().apply {
                    putBoolean(SharePrefKeys.PermanentSkipLogin.name, false)
                }.apply()
                callback(null)
            }?.addOnFailureListener(callback)
        }
    }
}
