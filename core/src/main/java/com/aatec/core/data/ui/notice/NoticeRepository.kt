/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 10:51 AM
 */



package com.aatec.core.data.ui.notice

import com.aatec.bit.data.Newtork.Notice.Notice3NetworkMapper
import com.aatec.core.data.network.notice.Attach
import com.aatec.core.data.network.notice.Notice3NetworkEntity
import com.aatec.core.data.room.notice.Notice3CacheMapper
import com.aatec.core.data.room.notice.Notice3Dao
import com.aatec.core.utils.DataState
import com.aatec.core.utils.NoItemFoundException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class NoticeRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val notice3Dao: Notice3Dao,
    private val cacheMapper3: Notice3CacheMapper,
    private val networkMapper3: Notice3NetworkMapper,
) {


    suspend fun getNoticeSearch(query: String): Flow<List<Notice3>> = flow {
        val searchNotice = notice3Dao.getNoticeTitle(query)
        emit(cacheMapper3.mapFromEntityList(searchNotice))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getNotice3(): Flow<DataState<List<Notice3>>> =
        channelFlow {
            try {
                val ref =
                    db.collection("BIT_Notice_New").orderBy("created", Query.Direction.DESCENDING)
                ref.addSnapshotListener { value, error ->
                    launch(Dispatchers.Main) {
                        if (error != null) {
                            send(DataState.Error(error))
                        } else {
                            if (value != null) {
                                send(DataState.Loading)
                                val networkNotice =
                                    value.toObjects(Notice3NetworkEntity::class.java)
                                val notices = networkMapper3.mapFromEntityList(networkNotice)
                                notice3Dao.deleteAll()
                                for (notice in notices) {
                                    notice3Dao.insert(cacheMapper3.mapToEntity(notice))
                                }
                                val cachedBlog = notice3Dao.get()
                                send(DataState.Success(cacheMapper3.mapFromEntityList(cachedBlog)))
                                if (value.isEmpty) {
                                    send(DataState.Empty)
                                }
                            }
                        }
                    }
                }
                awaitClose()
            } catch (e: Exception) {
                send(DataState.Empty)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNoticeDeepLink(): Flow<DataState<List<Notice3>>> = flow {
        try {
            addNotice()
            val allNotice = notice3Dao.getNoticeFlow()
            allNotice.collect {
                emit(DataState.Success(cacheMapper3.mapFromEntityList(it)))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    private fun addNotice() {
        val ref =
            db.collection("BIT_Notice_New").orderBy("created", Query.Direction.DESCENDING)
        ref.addSnapshotListener { value, _ ->
            runBlocking {
                if (value != null) {
                    val networkNotice =
                        value.toObjects(Notice3NetworkEntity::class.java)
                    val notices = networkMapper3.mapFromEntityList(networkNotice)
                    notice3Dao.deleteAll()
                    for (notice in notices) {
                        notice3Dao.insert(cacheMapper3.mapToEntity(notice))
                    }
                }
            }
        }

    }

    fun getNoticeFromPath(path: String): Flow<DataState<Notice3>> = channelFlow {
        try {
            db.collection("BIT_Notice_New").document(path)
                .addSnapshotListener { documentSnapShot, error ->
                    launch(Dispatchers.Main) {
                        send(DataState.Loading)
                        val notice = documentSnapShot?.toObject(Notice3NetworkEntity::class.java)
                        if (notice == null) {
                            send(DataState.Error(NoItemFoundException("Invalid Link")))
                        } else {
                            send(DataState.Success(networkMapper3.mapFormEntity(notice)))
                        }
                    }
                }
            awaitClose()
        } catch (e: Exception) {
            send(DataState.Error(e))
        }
    }

    fun getAttachFromPath(path: String): Flow<DataState<List<Attach>>> = channelFlow {
        try {
            db.collection("BIT_Notice_New").document(path)
                .collection("attach")
                .addSnapshotListener { documentSnapShot, error ->
                    launch(Dispatchers.Main) {
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
}