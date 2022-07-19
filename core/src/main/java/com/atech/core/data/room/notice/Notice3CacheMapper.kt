/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/13/22, 10:36 AM
 */

package com.atech.core.data.room.notice

import com.atech.core.utils.convertDateToTime
import com.atech.core.utils.convertStringToLongMillis
import com.atech.core.data.ui.notice.Notice3
import com.atech.core.utils.EntityMapper
import java.util.*
import javax.inject.Inject

class Notice3CacheMapper @Inject constructor() : EntityMapper<Notice3CacheEntity, Notice3> {
    override fun mapFormEntity(entity: Notice3CacheEntity): Notice3 =
        Notice3(
            title = entity.title,
            body = entity.body,
            link = entity.link,
            sender = entity.sender,
            path = entity.path,
            created = entity.created.convertDateToTime()
                .convertStringToLongMillis()!!
        )

    override fun mapToEntity(domainModel: Notice3): Notice3CacheEntity =
        Notice3CacheEntity(
            domainModel.title,
            domainModel.body,
            link = domainModel.link,
            sender = domainModel.sender,
            path = domainModel.path,
            Date(domainModel.created),
        )

    fun mapFromEntityList(entities: List<Notice3CacheEntity>): List<Notice3> =
        entities.map {
            mapFormEntity(it)
        }
}