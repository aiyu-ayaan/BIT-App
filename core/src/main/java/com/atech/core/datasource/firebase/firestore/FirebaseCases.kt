package com.atech.core.datasource.firebase.firestore


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.first
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
    suspend operator fun invoke() =
        db.collection(Db.Event.value).snapshots().map { it.toObjects(EventModel::class.java) }.map {
            it.forEach { event ->
                event.attach = getAttach(Db.Event, event.path ?: "").first()
            }
            it
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
