package com.atech.core.api.aboutus

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

data class AboutUsModel(
    val devs: List<Devs>,
    val managers: List<Devs>,
    val contributors: List<Devs?>?,
)

@Keep
@Parcelize
data class Devs(
    val sno: Int,
    val name: String,
    val img_link: String,
    val website: String,
    val stackoverflow: String,
    val github: String,
    val linkedin: String,
    val instagram: String,
    val des: String
) : Parcelable