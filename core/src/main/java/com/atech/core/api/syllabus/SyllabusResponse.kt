package com.atech.core.api.syllabus

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SyllabusResponse(val semester: Semester?) : Parcelable

@Keep
@Parcelize
data class Semester(
    val id: String,
    val subjects: Subject
) : Parcelable

@Parcelize
@Keep
data class Subject(
    val theory: List<SubjectModel>,
    val lab: List<SubjectModel>,
    val pe: List<SubjectModel>
) : Parcelable


@Keep
@Parcelize
data class SubjectModel(
    val subjectName: String,
    val code: String,
    val shortName: String,
    val credit: Int,
) : Parcelable


class DiffUtilTheorySyllabusCallback : DiffUtil.ItemCallback<SubjectModel>() {
    override fun areItemsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
        return oldItem == newItem
    }
}
