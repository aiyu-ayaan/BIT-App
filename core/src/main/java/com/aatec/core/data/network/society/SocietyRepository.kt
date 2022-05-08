/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 1/22/22, 12:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 1/22/22, 11:51 AM
 */



package com.aatec.core.data.network.society

import com.aatec.core.utils.DataState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SocietyRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    suspend fun getSocieties(): Flow<DataState<List<SocietyNetworkEntity>>> = channelFlow {
        send(DataState.Loading)
        try {
            val ref = db.collection("Society").orderBy("sno", Query.Direction.ASCENDING)
            ref.addSnapshotListener { value, error ->
                runBlocking {
                    if (error != null) {
                        send(DataState.Error(error))
                    } else {
                        if (value != null) {
                            send(DataState.Loading)
                            val societies = value.toObjects(SocietyNetworkEntity::class.java)
                            send(DataState.Success(societies))
                            if (value.isEmpty) {
                                send(DataState.Empty)
                            }
                        }
                    }
                }
            }
            awaitClose()
        } catch (e: Exception) {
            send(DataState.Error(e))
            awaitClose()
        }
    }.flowOn(Dispatchers.Main)
}