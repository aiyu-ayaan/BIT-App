package com.atech.core.api

import androidx.room.withTransaction
import com.atech.core.api.syllabus.SyllabusApi
import com.atech.core.utils.networkBoundResource
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val api: SyllabusApi, private val db: ApiCacheDatabase
) {
    private val syllabusDao = db.syllabusCacheDao()

    fun getSyllabus(id : String) = networkBoundResource(query = {
        syllabusDao.getSyllabus(id)
    }, fetch = {
        api.getSubjects(id).semesters
    }, saveFetchResult = { syllabus ->
        db.withTransaction {
//            syllabusDao.deleteAll()
            syllabusDao.insertSyllabus(syllabus)
        }
    })
}