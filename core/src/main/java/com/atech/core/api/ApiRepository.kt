package com.atech.core.api

import android.util.Log
import androidx.room.withTransaction
import com.atech.core.api.holiday.Holiday
import com.atech.core.api.holiday.HolidayModel
import com.atech.core.utils.networkBoundResource
import com.atech.core.utils.networkFetchData
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

    fun getAboutUsData() = networkFetchData(
        fetch = {
            api.getAboutUs()
        }
    )

    fun getHolidayData(
        query: String = "all", filter: (query: String, HolidayModel) -> List<Holiday> = { q, h ->
            h.holidays.filter { holiday ->
                holiday.type == q
            }
        }
    ) = networkFetchData(
        fetch = {
            api.getHoliday()
        },
        action = { holidays ->
            HolidayModel(filter(query,holidays))
        }
    )


}