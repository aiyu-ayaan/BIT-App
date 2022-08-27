package com.atech.bit.ui.fragments.login

import androidx.lifecycle.ViewModel
import com.atech.core.data.network.user.UserModel
import com.atech.core.data.network.user.UserRepository
import com.bumptech.glide.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    fun addUser(
        user: UserModel,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) =
        repository.addUserToDatabase(user, onSuccess, onFailure)

}