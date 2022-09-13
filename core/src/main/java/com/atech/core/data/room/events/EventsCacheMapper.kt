package com.atech.core.data.room.events

import com.atech.core.data.ui.events.Events
import com.atech.core.utils.EntityMapper
import com.atech.core.utils.convertDateToTime
import com.atech.core.utils.convertStringToLongMillis
import java.sql.Date
import javax.inject.Inject

class EventsCacheMapper @Inject constructor() : EntityMapper<Events, EventsCacheEntity> {

    override fun mapFormEntity(entity: Events): EventsCacheEntity =
        EventsCacheEntity(
            Date(entity.created),
            entity.title,
            entity.content,
            entity.insta_link,
            entity.logo_link,
            entity.path,
            entity.society,
            entity.video_link
        )

    override fun mapToEntity(domainModel: EventsCacheEntity): Events =
        Events(
            domainModel.created.time,
            domainModel.title,
            domainModel.content,
            domainModel.insta_link,
            domainModel.logo_link,
            domainModel.path,
            domainModel.society,
            domainModel.video_link
        )

    fun mapFromEntityList(entities: List<EventsCacheEntity>): List<Events> =
        entities.map {
            mapToEntity(it)
        }

}