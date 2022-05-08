/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/13/22, 10:36 AM
 */

package com.aatec.core.data.room.timeTable

import com.aatec.core.data.ui.timeTable.TimeTableModel
import com.aatec.core.utils.EntityMapper
import javax.inject.Inject

class TimeTableCacheMapper @Inject constructor() :
    EntityMapper<TimeTableCacheEntity, TimeTableModel> {


    fun mapFromEntityList(entities: List<TimeTableCacheEntity>): List<TimeTableModel> =
        entities.map {
            mapFormEntity(it)
        }

    override fun mapFormEntity(entity: TimeTableCacheEntity): TimeTableModel =
        TimeTableModel(
            entity.course,
            entity.gender,
            entity.sem,
            entity.section,
            entity.imageLink,
            entity.created,
        )

    override fun mapToEntity(domainModel: TimeTableModel): TimeTableCacheEntity =
        TimeTableCacheEntity(
            domainModel.course,
            domainModel.gender,
            domainModel.sem,
            domainModel.section,
            domainModel.imageLink,
            domainModel.created
        )
}