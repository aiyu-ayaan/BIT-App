/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/3/22, 9:12 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/3/22, 9:12 PM
 */

package com.aatec.bit.data.Newtork.TimeTable

import com.aatec.core.data.ui.timeTable.TimeTableModel
import com.aatec.core.utils.EntityMapper
import javax.inject.Inject

class TimeTableTableMapper @Inject constructor() :
    EntityMapper<NetworkTimeTableEntity, TimeTableModel> {
    override fun mapFormEntity(entity: NetworkTimeTableEntity): TimeTableModel =
        TimeTableModel(
            entity.course,
            entity.gender,
            entity.sem,
            entity.section,
            entity.imageLink,
            entity.created
        )

    override fun mapToEntity(domainModel: TimeTableModel): NetworkTimeTableEntity =
        NetworkTimeTableEntity()


    fun mapFromEntityList(entities: List<NetworkTimeTableEntity>): List<TimeTableModel> =
        entities.map {
            mapFormEntity(it)
        }

}