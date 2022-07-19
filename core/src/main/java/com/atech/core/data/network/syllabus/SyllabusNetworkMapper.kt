/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.data.network.syllabus

import com.atech.core.data.room.syllabus.Subject
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.utils.EntityMapper
import javax.inject.Inject

class SyllabusNetworkMapper @Inject constructor() :
    EntityMapper<NetworkSyllabusEntity, SyllabusModel> {
    override fun mapFormEntity(entity: NetworkSyllabusEntity): SyllabusModel =
        SyllabusModel(
            subject = entity.subject,
            code = entity.code,
            credits = entity.credits,
            openCode = entity.openCode,
            type = entity.type,
            shortName = entity.shortName,
            listOrder = entity.listOrder,
            fromNetwork = entity.fromNetwork,
            subjectContent = Subject(
                entity.content,
                entity.book,
                entity.reference
            ),
            deprecated = entity.deprecated
        )

    override fun mapToEntity(domainModel: SyllabusModel): NetworkSyllabusEntity =
        NetworkSyllabusEntity().apply {}

    fun mapFromEntityList(entities: List<NetworkSyllabusEntity>): List<SyllabusModel> =
        entities.map {
            mapFormEntity(it)
        }

}