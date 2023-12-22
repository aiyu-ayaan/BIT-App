package com.atech.core.usecase


import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.datasource.firebase.firestore.Attach
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.datasource.firebase.firestore.NoticeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class FirebaseLoginUseCase @Inject constructor(
    val addUser: AddUser,
    val checkUserData: CheckUserData,
)


data class FirebaseCase @Inject constructor(
    val getEvent: GetEvent,
    val getNotice: GetNotice,
    val getAttach: GetAttach
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
        ref.document(user.uid!!).set(user).await()
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
    ): (Pair<Boolean?, Exception?>) =
        try {
            val d = db.collection(Db.User.value).document(uid).collection(Db.Data.value)
                .document(uid).get().await()
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
