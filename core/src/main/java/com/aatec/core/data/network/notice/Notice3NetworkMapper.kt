/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/13/22, 10:37 AM
 */

package com.aatec.bit.data.Newtork.Notice

import com.aatec.core.data.network.notice.Notice3NetworkEntity
import com.aatec.core.data.ui.notice.Notice3
import com.aatec.core.utils.EntityMapper
import javax.inject.Inject

class Notice3NetworkMapper @Inject constructor() : EntityMapper<Notice3NetworkEntity, Notice3> {
    override fun mapFormEntity(entity: Notice3NetworkEntity): Notice3 =
        Notice3(
            entity.title,
            entity.body,
            link = entity.link,
            created = entity.created,
            sender = entity.sender,
            path = entity.path
        )

    override fun mapToEntity(domainModel: Notice3): Notice3NetworkEntity =
        Notice3NetworkEntity(

        )

    fun mapFromEntityList(entities: List<Notice3NetworkEntity>): List<Notice3> =
        entities.map {
            mapFormEntity(it)
        }

}