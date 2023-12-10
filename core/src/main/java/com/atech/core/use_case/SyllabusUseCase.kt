package com.atech.core.use_case

import androidx.annotation.Keep
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.atech.core.room.syllabus.Subject
import com.atech.core.room.syllabus.SubjectType
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.room.syllabus.SyllabusModel
import com.atech.core.utils.DEFAULTPAGESIZE
import com.atech.core.utils.EntityMapper
import com.atech.core.utils.INITIALlOADSIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class SyllabusUseCase @Inject constructor(
    val getSubjectsByType: GetSubjectsByType
)


data class GetSubjectsByType @Inject constructor(
    private val dao: SyllabusDao,
    private val offlineMapper: OfflineSyllabusUIMapper
) {
    operator fun invoke(
        courseSem : String,
        type: SubjectType = SubjectType.THEORY
    ): Flow<PagingData<SyllabusUIModel>> = Pager(
        config = PagingConfig(
            pageSize = DEFAULTPAGESIZE,
            enablePlaceholders = false,
            initialLoadSize = INITIALlOADSIZE
        )
    ) {
        dao.getSyllabusType(courseSem, type.name)
    }.flow.map {
        it.map { model ->
            offlineMapper.mapFormEntity(model)
        }
    }

}

@Keep
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
)

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