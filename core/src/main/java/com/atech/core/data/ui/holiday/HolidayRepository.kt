/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/8/22, 11:30 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/8/22, 9:47 AM
 */



package com.atech.core.data.ui.holiday

import com.atech.core.data.network.holiday.HolidayNetworkEntity
import com.atech.core.data.network.holiday.HolidayNetworkMapper
import com.atech.core.data.room.holiday.HolidayCacheMapper
import com.atech.core.data.room.holiday.HolidayDao
import com.atech.core.utils.DataState
import com.atech.core.utils.handler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class HolidayRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val holidayDao: HolidayDao,
    private val holidayNetworkMapper: HolidayNetworkMapper,
    private val holidayCacheMapper: HolidayCacheMapper
) {

    private fun addHoliday() {
        try {
            val ref = db.collection("BIT_Holiday").orderBy("sno", Query.Direction.ASCENDING)
            ref.addSnapshotListener { value, _ ->
                runBlocking ( handler){
                    if (value != null) {
                        val networkHoliday =
                            value.toObjects(HolidayNetworkEntity::class.java)
                        val holidays =
                            holidayNetworkMapper.mapFromEntityList(networkHoliday)
                        holidayDao.deleteAll()
                        for (holiday in holidays) {
                            holidayDao.insert(
                                holidayCacheMapper.mapFormEntity(
                                    holiday
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


    fun getHoliday(query: String): Flow<DataState<List<Holiday>>> = flow {
        addHoliday()
        val cacheHoliday = holidayDao.get(query)
        emit(DataState.Loading)
        kotlinx.coroutines.delay(500)
        cacheHoliday.collect {
            emit(
                DataState.Success(
                    holidayCacheMapper.mapFromEntityList(
                        it
                    )
                )
            )
            if (it.isEmpty()) {
                emit(DataState.Empty)
            }
        }
    }.flowOn(Dispatchers.Main)

    suspend fun getHolidayByMonth(month: String): Flow<DataState<List<Holiday>>> = flow {
        addHoliday()
        val cachedHoliday = holidayDao.getMonth(month)
        try {
            cachedHoliday.collect {
                when {
                    it.isEmpty() -> {
                        emit(DataState.Empty)
                    }
                    else -> {
                        emit(
                            DataState.Success(
                                holidayCacheMapper.mapFromEntityList(it)
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }


    suspend fun getSearchHoliday(query: String): Flow<List<Holiday>> = flow {
        val searchHoliday = holidayDao.getSearchHoliday(query)
        emit(holidayCacheMapper.mapFromEntityList(searchHoliday))
    }
}