package com.atech.core.data.network.events

import com.atech.core.data.ui.events.Events
import com.atech.core.utils.EntityMapper
import javax.inject.Inject

class EventsNetworkMapper @Inject constructor() : EntityMapper<EventsNetworkEntity, Events> {
    override fun mapFormEntity(entity: EventsNetworkEntity): Events =
        Events(
            entity.created ?: System.currentTimeMillis(),
            entity.title ?: "",
            entity.content ?: "",
            entity.insta_link ?: "",
            entity.logo_link ?: "",
            entity.path ?: "",
            entity.poster_link ?: "",
            entity.society ?: "",
            entity.video_link ?: "",
        )


    override fun mapToEntity(domainModel: Events): EventsNetworkEntity =
        EventsNetworkEntity(
            domainModel.created,
            domainModel.title,
            domainModel.content,
            domainModel.insta_link,
            domainModel.logo_link,
            domainModel.path,
            domainModel.poster_link,
            domainModel.society,
            domainModel.video_link,
        )

    fun mapFromEntityList(entities: List<EventsNetworkEntity>): List<Events> =
        entities.map {
            mapFormEntity(it)
        }

}