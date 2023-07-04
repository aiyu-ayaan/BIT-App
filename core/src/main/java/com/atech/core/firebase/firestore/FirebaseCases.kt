package com.atech.core.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class Db(val value: String) {
    Event("BIT_Events"), Notice("BIT_Notice_New"),
}

data class EventCases @Inject constructor(
    val getEvent: GetEvent,
    val getEventDetails: GetEventDetails,
    val getAttach: GetAttach,
)

class GetEvent @Inject constructor(
    private val db: FirebaseFirestore
) {
    @Throws(Exception::class)
    operator fun invoke(query: String = ""): Flow<List<EventModel>?> = try {
        db.collection(Db.Event.value).orderBy("created", Query.Direction.DESCENDING).snapshots()
            .map {
                it.toObjects(EventModel::class.java).let { events ->
                    if (query.isNotEmpty()) {
                        events.filter { event ->
                            event.title?.contains(query, true) ?: false
                        }
                    } else {
                        events
                    }
                }
            }
    } catch (e: Exception) {
        throw e
    }
}

class GetEventDetails @Inject constructor(
    private val db: FirebaseFirestore
) {

    @Throws(Exception::class)
    operator fun invoke(path: String) =
        try {
            db.collection(Db.Event.value).document(path).snapshots().map {
                it.toObject<EventModel>()
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
