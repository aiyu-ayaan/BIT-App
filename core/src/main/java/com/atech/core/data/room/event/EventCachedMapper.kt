/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 9:55 AM
 */



package com.atech.core.data.room.event

import com.atech.core.utils.convertDateToTime
import com.atech.core.utils.convertLongToTime
import com.atech.core.utils.convertStringToLongMillis
import com.atech.core.data.ui.event.Event
import com.atech.core.utils.EntityMapper

import java.util.*
import javax.inject.Inject

class EventCachedMapper @Inject constructor() : EntityMapper<Event, EventCachedEntity> {
    override fun mapFormEntity(entity: Event): EventCachedEntity =
        EventCachedEntity(
            Date(entity.created),
            entity.created.convertLongToTime("MMM dd,YYYY hh:mm aa"),
            entity.event_body,
            entity.event_title,
            entity.ins_link,
            entity.logo_link,
            entity.society,
            entity.web_link,
            entity.poster_link,
            entity.path
        )

    override fun mapToEntity(domainModel: EventCachedEntity): Event =
        Event(
            domainModel.created.convertDateToTime()
                .convertStringToLongMillis()!!,
            domainModel.date,
            domainModel.event_body,
            domainModel.event_title,
            domainModel.ins_link,
            domainModel.logo_link,
            domainModel.society,
            domainModel.web_link,
            domainModel.poster_link ?: "",
            domainModel.path!!
        )

    fun mapFromEntityList(entities: List<EventCachedEntity>): List<Event> =
        entities.map {
            mapToEntity(it)
        }
}