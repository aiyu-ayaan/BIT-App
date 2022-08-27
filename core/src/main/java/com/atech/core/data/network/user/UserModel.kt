package com.atech.core.data.network.user

import androidx.annotation.Keep

@Keep
data class UserModel(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var profilePic: String? = null,
)