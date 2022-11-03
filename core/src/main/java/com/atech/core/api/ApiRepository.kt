package com.atech.core.api

import android.util.Log
import androidx.room.withTransaction
import com.atech.core.utils.DataState
import com.atech.core.utils.handler
import com.atech.core.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val api: BITApiClient, private val db: ApiCacheDatabase
) {
    private val syllabusDao = db.syllabusCacheDao()

    fun getSyllabus(id: String) = networkBoundResource(query = {
        syllabusDao.getSyllabus(id)
    }, fetch = {
        api.getSubjects(id).semesters
    }, saveFetchResult = { syllabus ->
        db.withTransaction {
            try {
                syllabusDao.insertSyllabus(syllabus)
            } catch (e: Exception) {
                Log.d("XXX", "getSyllabus: ${e.localizedMessage}")
            }
        }
    })

    fun getAboutUsData() = flow {
        emit(DataState.Loading)
        try {
            val data = api.getAboutUs()
            emit(DataState.Success(data))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(handler)
}