package com.atech.core.firebase.firestore

import com.atech.core.firebase.auth.UserData
import com.atech.core.firebase.auth.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class Db(val value: String) {
    Event("BIT_Events"), Notice("BIT_Notice_New"), User("BIT_User"), Data("data")
}

data class FirebaseCases @Inject constructor(
    val getAttach: GetAttach,
    val getData: GetData,
    val getDocumentDetails: GetDocumentDetails,
    val addUser: AddUser,
    val checkUserData: CheckUserData,
    val getUserSaveDetails: GetUserSaveDetails,
    val getUserDataFromDb: GetUserDataFromDb,
    val uploadData: UploadData
)


class GetData @Inject constructor(
    private val db: FirebaseFirestore
) {
    @Throws(Exception::class)
    operator fun <T> invoke(mapTo: Class<T>, ref: Db, query: String = ""): Flow<List<T>?> = try {
        db.collection(ref.value).orderBy("created", Query.Direction.DESCENDING).snapshots().map {
            it.toObjects(mapTo).let { data ->
                if (query.isNotEmpty()) {
                    data.filter { event ->
                        event.toString().contains(query, true)
                    }
                } else {
                    data
                }
            }
        }
    } catch (e: Exception) {
        throw e
    }
}


class GetDocumentDetails @Inject constructor(
    private val db: FirebaseFirestore
) {

    @Throws(Exception::class)
    operator fun <T> invoke(mapTo: Class<T>, ref: Db, path: String) = try {
        db.collection(ref.value).document(path).snapshots().map {
            it.toObject(mapTo)
        }
    } catch (e: Exception) {
        throw e
    }
}


class GetAttach @Inject constructor(
    private val db: FirebaseFirestore
) {
    @Throws(Exception::class)
    operator fun invoke(type: Db, path: String) = try {
        db.collection(type.value).document(path).collection("attach").snapshots().map {
            it.toObjects(Attach::class.java)
        }
    } catch (e: Exception) {
        throw e
    }
}

class AddUser @Inject constructor(
    private val db: FirebaseFirestore
) {
    operator fun invoke(
        user: UserModel, callback: (Pair<String, Exception?>) -> Unit
    ) {
        val ref = db.collection(Db.User.value)
        ref.document(user.uid!!).set(user).addOnSuccessListener {
            callback(Pair(user.uid!!, null))
        }.addOnFailureListener { exception ->
            callback(Pair("", exception))
        }
    }
}


class CheckUserData @Inject constructor(
    private val db: FirebaseFirestore
) {
    operator fun invoke(
        uid: String, callback: (Pair<Boolean?, Exception?>) -> Unit
    ) {
        db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val s = document.getString("courseSem")
                    if (s != null) callback(true to null)
                    else callback(false to null)
                } else {
                    callback(false to null)
                }
            }.addOnFailureListener { exception ->
                callback(null to exception)
            }
    }
}

class GetUserSaveDetails @Inject constructor(
    private val db: FirebaseFirestore
) {
    operator fun invoke(uid: String, callback: (Pair<UserData?, Exception?>) -> Unit) {
        db.collection(Db.User.value).document(uid).collection(Db.Data.value).document(uid).get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    callback(null to Exception("No data found"))
                    return@addOnSuccessListener
                }
                it.toObject(UserData::class.java)?.let { data ->
                    callback(data to null)
                }
            }.addOnFailureListener {
                callback(null to it)
            }
    }
}

class GetUserDataFromDb @Inject constructor(
    private val db: FirebaseFirestore
) {
    operator fun invoke(uid: String, callback: (Pair<UserModel?, Exception?>) -> Unit) {
        db.collection(Db.User.value).document(uid).get().addOnSuccessListener {
            if (!it.exists()) {
                callback(null to Exception("No data found"))
                return@addOnSuccessListener
            }
            it.toObject(UserModel::class.java)?.let { data ->
                callback(data to null)
            }
        }.addOnFailureListener {
            callback(null to it)
        }
    }
}


class UploadData @Inject constructor(
    private val db: FirebaseFirestore, private val checkUserData: CheckUserData
) {
    private fun updateSyncTime(uid: String, callback: (Exception?) -> Unit = {}) {
        db.collection(Db.User.value).document(uid).update("syncTime", System.currentTimeMillis())
            .addOnSuccessListener {
                callback(null)
            }.addOnFailureListener { error ->
                callback(error)
            }
    }

    fun updateCourse(uid: String, course: String, sem: String, callback: (Exception?) -> Unit) {
        val ref = db.collection(Db.User.value).document(uid).collection(Db.Data.value)
        val courseSem = "$course $sem"
        checkUserData(uid) { (data, exception) ->
            if (exception != null) {
                ref.document(uid).set(mapOf("courseSem" to courseSem)).addOnSuccessListener {
                    updateSyncTime(uid) {
                        if (it == null) callback(null)
                        else callback(it)
                    }
                }.addOnFailureListener { error ->
                    callback(error)
                }
                return@checkUserData
            }
            if (data == true) {
                ref.document(uid).update(mapOf("courseSem" to courseSem)).addOnSuccessListener {
                    updateSyncTime(uid) {
                        if (it == null) callback(null)
                        else callback(it)
                    }
                }.addOnFailureListener { error ->
                    callback(error)
                }
                return@checkUserData
            }
            ref.document(uid).set(mapOf("courseSem" to courseSem)).addOnSuccessListener {
                updateSyncTime(uid) {
                    if (it == null) callback(null)
                    else callback(it)
                }
            }.addOnFailureListener { error ->
                callback(error)
            }
        }
    }
}