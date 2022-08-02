/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/25/22, 9:19 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/25/22, 2:05 AM
 */



package com.atech.core.data.ui.aboutUs

import android.util.Log
import com.atech.core.data.network.aboutus.Devs
import com.atech.core.utils.DataState
import com.atech.core.utils.handler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class AboutUsRepository @Inject constructor(
    private val db: FirebaseFirestore
) {


    fun getDevs(): Flow<DataState<List<Devs>>> = channelFlow {
        send(DataState.Loading)
        try {
            val ref = db.collection("AboutUs").orderBy("sno", Query.Direction.ASCENDING)
            val v = ref.addSnapshotListener { value, error ->
                launch(Dispatchers.Main + handler) {
                    if (error != null) {
                        send(DataState.Error(error))
                    } else {
                        if (value != null) {
                            send(DataState.Loading)
                            val devs = value.toObjects(Devs::class.java)
                            send(DataState.Success(devs))
                            Log.d("TAG", "getDevs: ${value.documents.size}")
                            if (value.isEmpty) {
                                send(DataState.Empty)
                            }
                        }
                    }
                }
            }
            awaitClose { v.remove() }
        } catch (e: Exception) {
            send(DataState.Error(e))
            awaitClose()
        }
    }.flowOn(Dispatchers.Main)


    fun getContributors(): Flow<DataState<List<Devs>>> = channelFlow {
        send(DataState.Loading)
        try {
            val ref = db.collection("AboutUsContributors").orderBy("sno", Query.Direction.ASCENDING)
            val v = ref.addSnapshotListener { value, error ->
                launch(Dispatchers.Main + handler) {
                    if (error != null) {
                        send(DataState.Error(error))
                    } else {
                        if (value != null) {
                            send(DataState.Loading)
                            val devs = value.toObjects(Devs::class.java)
                            send(DataState.Success(devs))
                            Log.d("TAG", "getDevs: ${value.documents.size}")
                            if (value.isEmpty) {
                                send(DataState.Empty)
                            }
                        }
                    }
                }
            }
            awaitClose { v.remove() }
        } catch (e: Exception) {
            send(DataState.Error(e))
            awaitClose()
        }
    }.flowOn(Dispatchers.Main)


    fun getManagers(): Flow<DataState<List<Devs>>> = channelFlow {
        send(DataState.Loading)
        try {
            val ref = db.collection("AboutUsManagers").orderBy("sno", Query.Direction.ASCENDING)
            val v = ref.addSnapshotListener { value, error ->
                launch(Dispatchers.Main + handler) {
                    if (error != null) {
                        send(DataState.Error(error))
                    } else {
                        if (value != null) {
                            send(DataState.Loading)
                            val devs = value.toObjects(Devs::class.java)
                            send(DataState.Success(devs))
                            Log.d("AboutUs", "getManagers: ${value.documents.size}")
                            if (value.isEmpty) {
                                send(DataState.Empty)
                            }
                        }
                    }
                }
            }
            awaitClose { v.remove() }
        } catch (e: Exception) {
            send(DataState.Error(e))
            awaitClose()
        }
    }.flowOn(Dispatchers.Main)
}