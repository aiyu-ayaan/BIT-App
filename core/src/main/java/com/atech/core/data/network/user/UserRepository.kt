package com.atech.core.data.network.user

import com.atech.core.utils.DataState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) {


    fun addUserToDatabase(
        user: UserModel,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User")
        ref.document(user.uid!!).set(user)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun getUserFromDatabase(uid: String): Flow<DataState<UserModel>> = channelFlow {
        val ref = db.collection("AttendanceManager")
            .document("Users")
        ref.collection(uid).document(uid).get()
            .addOnSuccessListener {
                launch(Dispatchers.Main) {
                    send(DataState.Success(it.toObject(UserModel::class.java)!!))
                }
            }
            .addOnFailureListener { exception ->
                launch(Dispatchers.Main) {
                    send(DataState.Error(exception))
                }
            }
    }.flowOn(Dispatchers.Main)

}