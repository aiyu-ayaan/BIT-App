package com.atech.core.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class Db(val value: String) {
    Event("BIT_Events"), Notice("BIT_Notice_New"),
}

data class FirebaseCases @Inject constructor(
    val getAttach: GetAttach,
    val getData: GetData,
    val getDocumentDetails: GetDocumentDetails,
)


class GetData @Inject constructor(
    private val db: FirebaseFirestore
) {
    @Throws(Exception::class)
    operator fun <T> invoke(mapTo: Class<T>, ref: Db, query: String = ""): Flow<List<T>?> = try {
        db.collection(ref.value).orderBy("created", Query.Direction.DESCENDING)
            .snapshots()
            .map {
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
    operator fun <T> invoke(mapTo: Class<T>, ref: Db, path: String) =
        try {
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
