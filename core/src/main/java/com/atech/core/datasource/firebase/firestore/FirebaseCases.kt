package com.atech.core.datasource.firebase.firestore


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class FirebaseCase @Inject constructor(
    val getEvent: GetEvent,
)

enum class Db(val value: String) {
    Event("BIT_Events"), Notice("BIT_Notice_New"), User("BIT_User"), Data("data")
}


data class GetEvent @Inject constructor(
    private val db: FirebaseFirestore, private val getAttach: GetAttach
) {
    operator fun invoke() =
        db.collection(Db.Event.value).orderBy("created", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(EventModel::class.java) }.map {
                it.forEach { event ->
                    getAttach(Db.Event,
                        event.path ?: "",
                        action = { attach ->
                            Log.d("AAA", "invoke: $attach")
                            event.attach = attach
                        }
                    )
                }
                it
            }
}


class GetAttach @Inject constructor(
    private val db: FirebaseFirestore
) {
    @Throws(Exception::class)
    operator fun invoke(type: Db, path: String, action: (List<Attach>) -> Unit = {}) {
        db.collection(type.value).document(path).collection("attach")
            .addSnapshotListener { value, error ->
                if (error != null)
                    action(emptyList())
                if (value != null)
                    action(value.toObjects(Attach::class.java))
            }
    }
}
