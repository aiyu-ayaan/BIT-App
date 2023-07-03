package com.atech.course.sem.adapter

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import com.atech.core.retrofit.client.SubjectModel
import com.atech.core.room.syllabus.Subject
import com.atech.core.room.syllabus.SyllabusModel
import com.atech.core.utils.EntityMapper
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

sealed class CourseItem {
    data class Title(val title: String) : CourseItem()
    data class Subject(val data: SyllabusUIModel) : CourseItem()
}

/**
 * Syllabus UI Model use to combine online and offline data
 */
@Keep
@Parcelize
data class SyllabusUIModel(
    val subject: String,
    val code: String,
    val credits: Int,
    val openCode: String,
    val type: String,
    val group: String,
    val shortName: String?,
    val listOrder: Int,
    val subjectContent: Subject?,
    val isChecked: Boolean?,
    val isAdded: Boolean?,
    val fromNetwork: Boolean?,
    val deprecated: Boolean?,
    val isFromOnline: Boolean = false
) : Parcelable

class SyllabusUIDiffUnit : DiffUtil.ItemCallback<SyllabusUIModel>() {
    override fun areItemsTheSame(oldItem: SyllabusUIModel, newItem: SyllabusUIModel): Boolean =
        oldItem.subject == newItem.subject

    override fun areContentsTheSame(oldItem: SyllabusUIModel, newItem: SyllabusUIModel): Boolean =
        oldItem == newItem

}

class OfflineSyllabusUIMapper @Inject constructor() : EntityMapper<SyllabusModel, SyllabusUIModel> {
    override fun mapFormEntity(entity: SyllabusModel): SyllabusUIModel =
        SyllabusUIModel(
            subject = entity.subject,
            code = entity.code,
            credits = entity.credits,
            openCode = entity.openCode,
            type = entity.type,
            group = entity.group,
            shortName = entity.shortName,
            listOrder = entity.listOrder,
            subjectContent = entity.subjectContent,
            isChecked = entity.isChecked,
            isAdded = entity.isAdded,
            fromNetwork = entity.fromNetwork,
            deprecated = entity.deprecated
        )

    override fun mapToEntity(domainModel: SyllabusUIModel): SyllabusModel =
        SyllabusModel(
            subject = domainModel.subject,
            code = domainModel.code,
            credits = domainModel.credits,
            openCode = domainModel.openCode,
            type = domainModel.type,
            group = domainModel.group,
            shortName = domainModel.shortName ?: "",
            listOrder = domainModel.listOrder,
            subjectContent = domainModel.subjectContent,
            isChecked = domainModel.isChecked,
            isAdded = domainModel.isAdded,
            fromNetwork = domainModel.fromNetwork,
            deprecated = domainModel.deprecated
        )

    fun mapFromEntityList(entities: List<SyllabusModel>): List<SyllabusUIModel> =
        entities.map { mapFormEntity(it) }

}

class OnlineSyllabusUIMapper @Inject constructor() : EntityMapper<SubjectModel, SyllabusUIModel> {
    override fun mapFormEntity(entity: SubjectModel): SyllabusUIModel =
        SyllabusUIModel(
            subject = entity.subjectName,
            code = entity.code,
            credits = entity.credit.toInt(),
            openCode = "",
            type = "",
            group = "",
            shortName = entity.shortName,
            listOrder = 0,
            subjectContent = null,
            isChecked = false,
            isAdded = false,
            fromNetwork = true,
            deprecated = false,
            isFromOnline = true
        )

    override fun mapToEntity(domainModel: SyllabusUIModel): SubjectModel =
        SubjectModel(
            subjectName = domainModel.subject,
            code = domainModel.code,
            credit = domainModel.credits.toDouble(),
            shortName = domainModel.shortName ?: ""
        )

    fun mapFromEntityList(entities: List<SubjectModel>): List<SyllabusUIModel> =
        entities.map { mapFormEntity(it) }
}