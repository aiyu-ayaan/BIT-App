package com.atech.course.sem.adapter

import android.os.Parcelable
import androidx.annotation.Keep
import com.atech.core.room.syllabus.Subject
import com.atech.core.room.syllabus.SyllabusModel
import com.atech.theme.EntityMapper
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
    val shortName: String,
    val listOrder: Int,
    val subjectContent: Subject?,
    val isChecked: Boolean?,
    val isAdded: Boolean?,
    val fromNetwork: Boolean?,
    val deprecated: Boolean?,
    val isFromOnline: Boolean = false
) : Parcelable

class SyllabusUIMapper @Inject constructor() : EntityMapper<SyllabusModel, SyllabusUIModel> {
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
            shortName = domainModel.shortName,
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