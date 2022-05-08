/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.core.data.network.holiday

import com.aatec.core.data.ui.holiday.Holiday
import com.aatec.core.utils.EntityMapper
import javax.inject.Inject

class HolidayNetworkMapper @Inject constructor() : EntityMapper<HolidayNetworkEntity, Holiday> {
    override fun mapFormEntity(entity: HolidayNetworkEntity): Holiday =
        Holiday(
            sno = entity.sno,
            day = entity.day,
            date = entity.date,
            occasion = entity.occasion,
            type = entity.type,
            month = entity.month
        )

    override fun mapToEntity(domainModel: Holiday): HolidayNetworkEntity =
        HolidayNetworkEntity().also {
            it.sno = domainModel.sno
            it.date = domainModel.date
            it.day = domainModel.day
            it.occasion = domainModel.occasion
            it.type = domainModel.type
        }

    fun mapFromEntityList(entities: List<HolidayNetworkEntity>): List<Holiday> =
        entities.map {
            mapFormEntity(it)
        }
}