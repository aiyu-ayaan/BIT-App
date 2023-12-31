package com.atech.core.usecase


import android.util.Log
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.auth.AttendanceUploadModel
import com.atech.core.datasource.firebase.auth.UserData
import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.datasource.firebase.firestore.Attach
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.datasource.firebase.firestore.NoticeModel
import com.atech.core.utils.hasSameDay
import com.atech.core.utils.toJSON
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class FirebaseLoginUseCase @Inject constructor(
    val addUser: AddUser,
    val checkUserData: CheckUserData,
    val uploadDataToFirebase: UploadDataToFirebase,
    val getUserEncryptedData: GetUserEncryptedData,
    val getUserSaveDetails: GetUserSaveDetails,
    val deleteUser: DeleteUserDataFromDatabase,
    val setChatSettings: SetChatSettings,
    val getChatSettings: GetChatSettings
)


data class FirebaseCase @Inject constructor(
    val getEvent: GetEvent,
    val getNotice: GetNotice,
    val getAttach: GetAttach,
    val getChatSettings: GetChatSettings
)

enum class Db(val value: String) {
    Event("BIT_Events"), Notice("BIT_Notice_New"), User("BIT_User"), Data("data")
}


data class GetEvent @Inject constructor(
    private val db: FirebaseFirestore, private val getAttach: GetAttach
) {
    operator fun invoke() =
        db.collection(Db.Event.value).orderBy("created", Query.Direction.DESCENDING).snapshots()
            .map { it.toObjects(EventModel::class.java) }
}

data class GetNotice @Inject constructor(
    private val db: FirebaseFirestore, private val getAttach: GetAttach
) {
    operator fun invoke(): Flow<List<NoticeModel>> =
        db.collection(Db.Notice.value).orderBy("created", Query.Direction.DESCENDING).snapshots()
            .map { it.toObjects(NoticeModel::class.java) }
}

class GetAttach @Inject constructor(
    private val db: FirebaseFirestore
) {
    @Throws(Exception::class)
    operator fun invoke(type: Db, path: String, action: (List<Attach>) -> Unit = {}) {
        try {
            db.collection(type.value).document(path).collection("attach")
                .addSnapshotListener { value, error ->
                    if (error != null) action(emptyList())
                    if (value != null) action(value.toObjects(Attach::class.java))
                }
        } catch (e: Exception) {
            action(emptyList())
        }
    }
}


class AddUser @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(
        user: UserModel
    ): Pair<String, Exception?> = try {
        val ref = db.collection(Db.User.value)
        ref.document(user.uid!!).set(user, SetOptions.merge()).await()
        Pair(user.uid!!, null)
    } catch (e: Exception) {
        Pair("", e)
    }
}

class CheckUserData @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(
        uid: String,
    ): (Pair<Boolean?, Exception?>) = try {
        val d =
            db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid).get()
                .await()
        if (d != null) {
            val s = d.getString("courseSem")
            if (s != null) Pair(true, null)
            else Pair(false, null)
        } else {
            Pair(false, null)
        }
    } catch (e: Exception) {
        Pair(null, e)
    }
}


class UploadDataToFirebase @Inject constructor(
    private val db: FirebaseFirestore, private val checkUserData: CheckUserData
) {
    private suspend fun updateSyncTime(uid: String): Exception? = try {
        db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid)
            .update("syncTime", System.currentTimeMillis()).await()
        null
    } catch (e: Exception) {
        e
    }

    suspend fun updateCourse(uid: String, course: String, sem: String): Exception? = try {
        val ref = db.collection(Db.User.value).document(uid).collection(Db.Data.value)
        val courseSem = "$course $sem"
        val (hasData, exception) = checkUserData.invoke(uid)
        if (exception != null) {
            ref.document(uid).set(mapOf("courseSem" to courseSem)).await()
        } else {
            if (hasData == true) ref.document(uid).update(mapOf("courseSem" to courseSem)).await()
            else ref.document(uid).set(mapOf("courseSem" to courseSem)).await()
        }
        updateSyncTime(uid)
        null
    } catch (e: Exception) {
        e
    }

    suspend fun updateCgpa(
        uid: String,
        cgpa: Cgpa,
    ): Exception? = try {
        val ref = db.collection(Db.User.value).document(uid).collection(Db.Data.value)
        val jsonCgpa = toJSON(cgpa)
        ref.document(uid).update(mapOf("cgpa" to jsonCgpa)).await()
        updateSyncTime(uid)
        null
    } catch (e: Exception) {
        e
    }

    suspend fun updateAttendance(
        uid: String,
        attendance: List<AttendanceUploadModel>,
    ): Exception? = try {
        val json = toJSON(attendance)
        db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid)
            .update(mapOf("attendance" to json))
        updateSyncTime(uid)
        null
    } catch (e: Exception) {
        e
    }
}

class GetUserEncryptedData @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(uid: String): Pair<UserModel?, Exception?> = try {
        val snapShot = db.collection(Db.User.value).document(uid).get().await()
        if (!snapShot.exists()) {
            null to Exception("No data found")
        } else {
            val model = snapShot.toObject(UserModel::class.java)
            model to null
        }
    } catch (e: Exception) {
        null to e
    }
}

data class GetUserSaveDetails @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(uid: String): Pair<UserData?, Exception?> = try {
        db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid).get()
            .await().toObject(UserData::class.java) to null
    } catch (e: Exception) {
        null to e
    }
}

data class DeleteUserDataFromDatabase @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(uid: String): Exception? = try {
        db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid).delete()
            .await()
        db.collection(Db.User.value).document(uid).delete().await()
        null
    } catch (e: Exception) {
        e
    }
}

data class SetChatSettings @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend fun updateChatEnable(
        uid: String,
        isChatEnable: Boolean,
    ) =
        try {
            db.collection(Db.User.value).document(uid).update(
                mapOf(
                    "isChatEnable" to isChatEnable
                )
            ).await()
            null
        } catch (e: Exception) {
            e
        }

    suspend fun updateLastChat(
        uid: String,
    ) =
        try {
            db.collection(Db.User.value).document(uid).get()
                .await().let {
                    val lastUpdate = it.get("lastChat") as Long?
                    if (lastUpdate == null) {
                        insertLastChat(uid, System.currentTimeMillis())
                        return@let
                    }
                    val currentTime = System.currentTimeMillis()
                    if (!(lastUpdate hasSameDay currentTime)) {
                        insertLastChat(uid, currentTime)
                        updateCurrentChatNumber(uid, 0)
                    }
                }
            null
        } catch (e: Exception) {
            e
        }

    private suspend fun insertLastChat(uid: String, lastChat: Long) {
        db.collection(Db.User.value).document(uid).update(
            mapOf(
                "lastChat" to lastChat
            )
        ).await()
    }

    suspend fun updateCurrentChatNumber(
        uid: String,
        currentChatNumber: Int,
    ) =
        try {
            db.collection(Db.User.value).document(uid).update(
                mapOf(
                    "currentChatNumber" to currentChatNumber
                )
            ).await()
            null
        } catch (e: Exception) {
            e
        }
}

data class GetChatSettings @Inject constructor(
    private val db: FirebaseFirestore
) {
    /**
     * @return Triple<Boolean, Long, Int>? -> isChatEnable, lastChat, currentChatNumber
     * @return Exception? -> Exception
     */
    suspend operator fun invoke(uid: String): Pair<Triple<Boolean, Long, Int>?, Exception?> = try {
        val snapShot = db.collection(Db.User.value).document(uid).get().await()
        if (!snapShot.exists()) {
            null to Exception("No data found")
        } else {
            val isChatEnable = (snapShot.get("isChatEnable") ?: false) as Boolean
            val lastChat = (snapShot.get("lastChat") ?: System.currentTimeMillis()) as Long
            val currentChatNumber = (snapShot.get("currentChatNumber") ?: 0) as Number
            Triple(isChatEnable, lastChat, currentChatNumber.toInt()) to null
        }
    } catch (e: Exception) {
        null to e
    }
}