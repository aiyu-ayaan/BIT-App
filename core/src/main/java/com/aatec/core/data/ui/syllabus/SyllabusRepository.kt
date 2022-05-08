/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.core.data.ui.syllabus

import android.util.Log
import com.aatec.core.data.network.syllabus.NetworkSyllabusEntity
import com.aatec.core.data.network.syllabus.SyllabusNetworkMapper
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.utils.BitAppScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyllabusRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val syllabusDao: SyllabusDao,
    private val networkMapper: SyllabusNetworkMapper,
    @BitAppScope private val appScope: CoroutineScope
) {
    private val TAG = "SyllabusRepository"
    suspend fun getSyllabus() {
        val ref = db.collection("BIT_Syllabus").orderBy("openCode", Query.Direction.DESCENDING)
        ref.addSnapshotListener { value, error ->
            if (error != null) Log.e(TAG, "getSyllabus:${error.message} ")
            else {
                value?.let {
                    appScope.launch {
                        try {
                            val listSyllabusNetworkEntity =
                                value.toObjects(NetworkSyllabusEntity::class.java)
                            val syllabus =
                                networkMapper.mapFromEntityList(listSyllabusNetworkEntity)
                            for (s in syllabus) {
                                syllabusDao.insert(s)
                                if (s.deprecated == true) syllabusDao.deleteNetwork(s)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "getSyllabus: ${e.message}")
                        }
                    }
                }
            }
        }
    }
}