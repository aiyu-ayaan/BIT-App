/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 8:09 PM
 */



package com.atech.core.data.ui.events

import android.util.Log
import com.atech.core.data.network.events.EventsNetworkEntity
import com.atech.core.data.network.events.EventsNetworkMapper
import com.atech.core.data.network.notice.Attach
import com.atech.core.data.room.events.EventsCacheMapper
import com.atech.core.data.room.events.EventsDao
import com.atech.core.utils.DataState
import com.atech.core.utils.NoItemFoundException
import com.atech.core.utils.handler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val dao: EventsDao,
    private val networkMapper: EventsNetworkMapper,
    private val cachedMapper: EventsCacheMapper
) {
    private fun addEventInDatabase() {
        try {
            val ref = db.collection("BIT_Events").orderBy("created", Query.Direction.DESCENDING)
            ref.addSnapshotListener { value, _ ->
                runBlocking(handler) {
                    if (value != null) {
                        val networkEvent =
                            value.toObjects(EventsNetworkEntity::class.java)
                        val events =
                            networkMapper.mapFromEntityList(networkEvent)
                        dao.deleteAll()
                        for (event in events) {
                            dao.insert(
                                cachedMapper.mapFormEntity(
                                    event
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }


    fun getEvents(): Flow<DataState<List<Events>>> = flow {
        try {
            addEventInDatabase()
            val cachedHoliday = dao.getEvents()
            cachedHoliday.collect {
                emit(
                    DataState.Success(
                        cachedMapper.mapFromEntityList(
                            it
                        )
                    )
                )
                if (it.isEmpty()) {
                    emit(DataState.Empty)
                }
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.Main)

    fun getEventFromPath(path: String): Flow<DataState<Events>> = channelFlow {
        try {
            db.collection("BIT_Events").document(path)
                .addSnapshotListener { documentSnapShot, _ ->
                    launch(Dispatchers.Main + handler) {
                        send(DataState.Loading)
                        val event = documentSnapShot?.toObject(EventsNetworkEntity::class.java)
                        if (event == null) {
                            send(DataState.Error(NoItemFoundException("Invalid Link")))
                        } else {
                            send(DataState.Success(networkMapper.mapFormEntity(event)))
                        }
                    }
                }
            awaitClose()
        } catch (e: Exception) {
            send(DataState.Error(e))
        }
    }

    suspend fun getSearchEvent(query: String): Flow<List<Events>> = flow {
        val searchQuery = dao.getSearchEvent(query)
        emit(cachedMapper.mapFromEntityList(searchQuery))
    }

    fun getAttachFromPath(path: String): Flow<DataState<List<Attach>>> = channelFlow {
        try {
            db.collection("BIT_Events").document(path)
                .collection("attach")
                .addSnapshotListener { documentSnapShot, _ ->
                    launch(Dispatchers.Main + handler) {
                        send(DataState.Loading)
                        val attach = documentSnapShot?.toObjects(Attach::class.java)
                        if (attach == null) {
                            send(DataState.Error(NoItemFoundException("No Attach")))
                        } else {
                            send(DataState.Success(attach))
                        }
                    }
                }
            awaitClose()
        } catch (e: Exception) {
            send(DataState.Error(e))
        }
    }


    fun getEvent7Days(start: Long, end: Long): Flow<List<Events>> = flow {
        addEventInDatabase()
        val cachedHoliday = dao.getEvents7Days(start, end)
        cachedHoliday.collect {
            emit(cachedMapper.mapFromEntityList(it))
        }
    }.flowOn(Dispatchers.Main)
}