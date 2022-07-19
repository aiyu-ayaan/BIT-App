/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.data.room.holiday

import com.atech.core.data.ui.holiday.Holiday
import com.atech.core.utils.EntityMapper
import javax.inject.Inject

class HolidayCacheMapper @Inject constructor() : EntityMapper<Holiday, HolidayCacheEntity> {
    override fun mapFormEntity(entity: Holiday): HolidayCacheEntity =
        HolidayCacheEntity(
            sno = entity.sno,
            day = entity.day,
            date = entity.date,
            occasion = entity.occasion,
            type = entity.type,
            month = entity.month
        )

    override fun mapToEntity(domainModel: HolidayCacheEntity): Holiday =
        Holiday(
            sno = domainModel.sno,
            day = domainModel.day,
            date = domainModel.date,
            occasion = domainModel.occasion,
            type = domainModel.type,
            month = domainModel.month
        )

    fun mapFromEntityList(entities: List<HolidayCacheEntity>): List<Holiday> =
        entities.map {
            mapToEntity(it)
        }
}
