package com.atech.core.api.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SyllabusResponse(val semesters: Semesters) : Parcelable

@Keep
@Parcelize
data class Semesters(
    val sem: Int,
    val subjects: Subject
) : Parcelable

@Parcelize
@Keep
data class Subject(
    val theory: List<Theory>,
    val lab: List<Lab>
) : Parcelable

@Keep
@Parcelize
data class Theory(
    val subjectName: String,
    val code: String,
    val openCode: String,
    val shortName: String,
    val credit: Int,
    val listOrder: Int,
    val content: List<TheoryContent>,
    val books: Books
) : Parcelable

@Keep
@Parcelize
data class TheoryContent(
    val module: String,
    val content: String,
) : Parcelable

@Keep
@Parcelize
data class Lab(
    val subjectName: String,
    val code: String,
    val openCode: String,
    val shortName: String,
    val credit: Int,
    val listOrder: Int,
    val content: List<LabContent>,
    val books: Books
) : Parcelable

@Keep
@Parcelize
data class LabContent(
    val question: String,
    val image: String?
) : Parcelable

@Keep
@Parcelize
data class Books(
    val textBooks: List<BookName>,
    val referenceBooks: List<BookName>
) : Parcelable

@Keep
@Parcelize
data class BookName(
    val bookName: String
) : Parcelable

class DiffUtilTheorySyllabusCallback() : DiffUtil.ItemCallback<Theory>() {
    override fun areItemsTheSame(oldItem: Theory, newItem: Theory): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Theory, newItem: Theory): Boolean {
        return oldItem == newItem
    }
}

class DiffUtilLabSyllabusCallback() : DiffUtil.ItemCallback<Lab>() {
    override fun areItemsTheSame(oldItem: Lab, newItem: Lab): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Lab, newItem: Lab): Boolean {
        return oldItem == newItem
    }
}