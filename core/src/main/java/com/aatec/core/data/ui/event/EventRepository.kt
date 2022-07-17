/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 8:09 PM
 */



package com.aatec.core.data.ui.event

import com.aatec.core.data.network.event.EventNetworkEntity
import com.aatec.core.data.network.event.EventNetworkMapper
import com.aatec.core.data.room.event.EventCachedMapper
import com.aatec.core.data.room.event.EventDao
import com.aatec.core.utils.DataState
import com.aatec.core.utils.NoItemFoundException
import com.aatec.core.utils.handler
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
    private val dao: EventDao,
    private val networkMapper: EventNetworkMapper,
    private val cachedMapper: EventCachedMapper
) {

    private fun addEventInDatabase() {
        try {
            val ref = db.collection("Events").orderBy("created", Query.Direction.DESCENDING)
            ref.addSnapshotListener { value, _ ->
                runBlocking( handler) {
                    if (value != null) {
                        val networkEvent =
                            value.toObjects(EventNetworkEntity::class.java)
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


    fun getEvents(): Flow<DataState<List<Event>>> = flow {
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

    fun getEventFromPath(path: String): Flow<DataState<Event>> = channelFlow {
        try {
            db.collection("Events").document(path)
                .addSnapshotListener { documentSnapShot, _ ->
                    launch(Dispatchers.Main+ handler) {
                        send(DataState.Loading)
                        val event = documentSnapShot?.toObject(EventNetworkEntity::class.java)
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

    suspend fun getSearchEvent(query: String): Flow<List<Event>> = flow {
        val searchQuery = dao.getSearchEvent(query)
        emit(cachedMapper.mapFromEntityList(searchQuery))
    }


    fun getEvent7Days(start: Long, end: Long): Flow<List<Event>> = flow {
        addEventInDatabase()
        val cachedHoliday = dao.getEvents7Days(start, end)
        cachedHoliday.collect {
            emit(cachedMapper.mapFromEntityList(it))
        }
    }.flowOn(Dispatchers.Main)
}