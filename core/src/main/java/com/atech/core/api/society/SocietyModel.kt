package com.atech.core.api.society

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
data class SocietyModel(
    val societies: List<Society>,
    val ngos: List<Society>
)

@Keep
@Parcelize
data class Society(
    val sno : Int,
    val name: String,
    val des: String,
    val ins: String,
    val logo_link: String
) : Parcelable
