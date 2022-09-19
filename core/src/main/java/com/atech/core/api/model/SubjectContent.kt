package com.atech.core.api.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SubjectContent(
    val subjectName: String,
    val theoryContents: List<TheoryContent> ?= null,
    val labContent : List<LabContent> ?= null,
    val books: Books
) : Parcelable