package com.atech.core.api.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SubjectContent(
    val subjectName: String,
    val content: List<TheoryContent>,
    val books: Books
) : Parcelable