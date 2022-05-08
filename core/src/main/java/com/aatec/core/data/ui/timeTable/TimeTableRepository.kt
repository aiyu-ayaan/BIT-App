/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/14/22, 1:24 PM
 */



package com.aatec.core.data.ui.timeTable

import com.aatec.bit.data.Newtork.TimeTable.NetworkTimeTableEntity
import com.aatec.bit.data.Newtork.TimeTable.TimeTableTableMapper
import com.aatec.core.data.room.timeTable.TimeTableCacheMapper
import com.aatec.core.data.room.timeTable.TimeTableDao
import com.aatec.core.utils.DataState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class TimeTableRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val timeTableDao: TimeTableDao,
    private val cacheMapper: TimeTableCacheMapper,
    private val networkMapper: TimeTableTableMapper,
) {


    fun getDefault(
        course: String,
        gender: String,
        sem: String,
        sec: String
    ): Flow<DataState<TimeTableModel>> = flow {
        try {
            addTimeTableInDatabase()
            timeTableDao.getTimeTableDefault(course, gender, sem, sec)
                .collect { it ->
                    if (it != null)
                        emit(
                            DataState.Success(
                                cacheMapper.mapFormEntity(
                                    it
                                )
                            )
                        )
                    else
                        emit(DataState.Error(NullPointerException("No Data Found")))
                }

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.catch { d ->
        emit(DataState.Error(d as Exception))
    }

    private fun addTimeTableInDatabase() {
        try {
            val ref = db.collection("BIT_Time_Table").orderBy("created", Query.Direction.DESCENDING)
            ref.addSnapshotListener { value, _ ->
                runBlocking {
                    if (value != null) {
                        val timeTableNetwork =
                            value.toObjects(NetworkTimeTableEntity::class.java)
                        val timeTable = networkMapper.mapFromEntityList(timeTableNetwork)
                        timeTableDao.deleteAll()
                        for (tp in timeTable) {
                            timeTableDao.insert(cacheMapper.mapToEntity(tp))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }


    fun getTimeTable(): Flow<DataState<List<TimeTableModel>>> =
        flow {
            try {
                addTimeTableInDatabase()
                val cachedTimeTable = timeTableDao.getAll()
                cachedTimeTable.collect { it ->
                    emit(
                        DataState.Success(
                            cacheMapper.mapFromEntityList(
                                it
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                emit(
                    DataState.Error(
                        e
                    )
                )
            }
        }.flowOn(Dispatchers.Main)

}