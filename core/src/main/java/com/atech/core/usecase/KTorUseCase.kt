package com.atech.core.usecase

import android.util.Log
import com.atech.core.datasource.retrofit.BitAppApiService
import com.atech.core.datasource.retrofit.model.Holiday
import com.atech.core.datasource.retrofit.model.HolidayType
import com.atech.core.datasource.retrofit.model.Subject
import java.util.Calendar
import javax.inject.Inject


class KTorUseCase @Inject constructor(
    val fetchSyllabus: FetchSyllabus,
    val fetchSubjectMarkDown: FetchSubjectMarkDown,
    val fetchHolidays: FetchHolidays,
    val fetchSociety: FetchSociety,
    val fetchAdministration: FetchAdministration,
)

data class FetchSyllabus @Inject constructor(
    private val api: BitAppApiService,
    private val mapper: OnlineSyllabusUIMapper
) {
    @Throws(Exception::class)
    suspend operator fun invoke(
        courseWithSem: String
    ): Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>> {
        val res = api.getSubjects(
            course = courseWithSem.replace("\\d".toRegex(), ""),
            courseSem = courseWithSem
        )
        if (res.semester?.subjects == null) {
            throw Exception("No data found")
        }
        return res.semester.subjects.mapToTriple(mapper)
    }
}


data class FetchSubjectMarkDown @Inject constructor(
    private val api: BitAppApiService
) {
    @Throws(Exception::class)
    suspend operator fun invoke(
        course: String,
        courseSem: String,
        subject: String
    ) = api.getSubjectMarkDown(course, courseSem, subject)

}

fun Subject.mapToTriple(mapper: OnlineSyllabusUIMapper) =
    Triple(
        mapper.mapFromEntityList(this.theory),
        mapper.mapFromEntityList(this.lab),
        mapper.mapFromEntityList(this.pe)
    )


data class FetchHolidays @Inject constructor(
    private val api: BitAppApiService,
) {
    @Throws(Exception::class)
    suspend operator fun invoke(
        type: HolidayType,
        month: String = ""
    ): List<Holiday> = try {
        api.getHoliday().holidays
            .let {
                if (type == HolidayType.ALL) it.filter { holiday -> holiday.month == month }
                else it.filter { holiday -> holiday.type == type.value }
            }
    } catch (e: Exception) {
        throw e
    }
}

data class FetchSociety @Inject constructor(
    private val api: BitAppApiService
) {
    @Throws(Exception::class)
    suspend operator fun invoke() = try {
        api.getSociety().societies to api.getSociety().ngos
    } catch (e: Exception) {
        throw e
    }
}

class FetchAdministration @Inject constructor(
    private val api: BitAppApiService
) {
    suspend operator fun invoke() = try {
        api.getAdministration()
    } catch (e: Exception) {
        throw e
    }
}
