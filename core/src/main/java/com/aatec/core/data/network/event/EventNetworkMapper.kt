/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 9:58 AM
 */



package com.aatec.core.data.network.event

import com.aatec.bit.utils.convertLongToTime
import com.aatec.core.data.ui.event.Event
import com.aatec.core.utils.EntityMapper
import javax.inject.Inject

class EventNetworkMapper @Inject constructor() : EntityMapper<EventNetworkEntity, Event> {
    override fun mapFormEntity(entity: EventNetworkEntity): Event =
        Event(
            entity.created,
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

    override fun mapToEntity(domainModel: Event): EventNetworkEntity =
        EventNetworkEntity().also {
            it.created
            it.event_body
            it.event_title
            it.ins_link
            it.logo_link
            it.society
            it.web_link
            it.poster_link
        }


    fun mapFromEntityList(entities: List<EventNetworkEntity>): List<Event> =
        entities.map {
            mapFormEntity(it)
        }
}