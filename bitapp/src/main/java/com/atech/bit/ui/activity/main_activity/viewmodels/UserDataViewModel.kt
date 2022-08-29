package com.atech.bit.ui.activity.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import com.atech.core.data.network.user.AttendanceUploadModel
import com.atech.core.data.network.user.UserModel
import com.atech.core.data.network.user.UserRepository
import com.atech.core.data.preferences.Cgpa
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    fun addUser(
        user: UserModel,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) =
        repository.addUserToDatabase(user, onSuccess, onFailure)

    fun getUser(
        uid: String,
        onSuccess: (UserModel) -> Unit,
        onFailure: (Exception) -> Unit
    ) = repository.getUserFromDatabase(uid, onSuccess, onFailure)

    fun checkUserData(
        uid: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) =
        repository.checkUserData(uid, onSuccess, onFailure)

    fun addCourseSem(
        uid: String,
        course: String,
        sem: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = repository.addCourseSem(uid, course, sem, onSuccess, onFailure)

    fun getCourseSem(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception /* = java.lang.Exception */) -> Unit
    ) = repository.getCourseSem(uid, onSuccess, onFailure)


    fun getCGPA(
        uid: String,
        onSuccess: (Cgpa) -> Unit,
        onFailure: (Exception) -> Unit
    ) = repository.getCGPA(uid, onSuccess, onFailure)


    fun setCGPA(
        uid: String,
        cgpa: Cgpa,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = repository.setCGPA(uid, cgpa, onSuccess, onFailure)


    fun setAttendance(
        uid: String,
        attendance: List<AttendanceUploadModel>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = repository.setAttendance(uid, attendance, onSuccess, onFailure)

    fun getAttendance(
        uid: String,
        onSuccess: (List<AttendanceUploadModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) = repository.getAttendance(uid, onSuccess, onFailure)

}