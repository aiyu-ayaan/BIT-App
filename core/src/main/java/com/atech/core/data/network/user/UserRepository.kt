package com.atech.core.data.network.user

import com.atech.core.data.preferences.Cgpa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) {


    fun addUserToDatabase(
        user: UserModel,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User")
        ref.document(user.uid!!).set(user)
            .addOnSuccessListener {
                onSuccess.invoke(user.uid!!)
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun getUserFromDatabase(
        uid: String,
        onSuccess: (UserModel) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User")
        ref.document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(UserModel::class.java)
                    user?.let { onSuccess.invoke(it) }
                } else {
                    onFailure.invoke(Exception("No such document"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun checkUserData(
        uid: String,
        hasData: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")

        ref.document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val s = document.getString("courseSem")
                    if (s != null)
                        hasData.invoke(true)
                    else
                        hasData.invoke(false)

                } else {
                    hasData.invoke(false)
                }
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun addCourseSem(
        uid: String,
        course: String,
        sem: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")
        val courseSem = "$course $sem"
        checkUserData(uid, {
            if (it)
                ref.document(uid).update(mapOf("courseSem" to courseSem))
                    .addOnSuccessListener {
                        onSuccess.invoke()
                    }
                    .addOnFailureListener { exception ->
                        onFailure.invoke(exception)
                    }
            else
                ref.document(uid).set(mapOf("courseSem" to courseSem))
                    .addOnSuccessListener {
                        onSuccess.invoke()
                    }
                    .addOnFailureListener { exception ->
                        onFailure.invoke(exception)
                    }
        }, {
            ref.document(uid).set(mapOf("courseSem" to courseSem))
                .addOnSuccessListener {
                    onSuccess.invoke()
                }
                .addOnFailureListener { exception ->
                    onFailure.invoke(exception)
                }
        })
    }

    fun getCourseSem(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")
        ref.document(uid).get()
            .addOnSuccessListener { document ->
                val s = document.getString("courseSem")
                if (s != null)
                    onSuccess.invoke(s)
                else
                    onFailure.invoke(java.lang.Exception("No Data Found !!"))
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun getCGPA(
        uid: String,
        onSuccess: (Cgpa) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")
        ref.document(uid).get()
            .addOnSuccessListener { document ->
                val s = document.getString("cgpa")
                if (s != null) {
                    val cgpa = Gson().fromJson(s, Cgpa::class.java)
                    onSuccess.invoke(cgpa)
                } else
                    onFailure.invoke(java.lang.Exception("No Data Found !!"))
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }


    fun setCGPA(
        uid: String,
        cgpa: Cgpa,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")
        val gson = Gson()
        val json = gson.toJson(cgpa)
        ref.document(uid).update(mapOf("cgpa" to json))
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun setAttendance(
        uid: String,
        attendance: List<AttendanceUploadModel>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")
        val gson = Gson()
        val json = gson.toJson(attendance)

        ref.document(uid).update(mapOf("attendance" to json))
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

    fun getAttendance(
        uid: String,
        onSuccess: (List<AttendanceUploadModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.collection("BIT_User").document(uid).collection("data")
        ref.document(uid).get()
            .addOnSuccessListener { document ->
                val s = document.getString("attendance")
                if (s != null) {
                    val attendance = Gson().fromJson(s, Array<AttendanceUploadModel>::class.java)
                    onSuccess.invoke(attendance.toList())
                } else
                    onFailure.invoke(java.lang.Exception("No Data Found !!"))
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }
}