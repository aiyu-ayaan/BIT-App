package com.atech.core.use_case

import com.atech.core.data_source.retrofit.BitAppApiService
import com.atech.core.data_source.retrofit.model.Subject
import javax.inject.Inject


class KTorUseCase @Inject constructor(
    val fetchSyllabus: FetchSyllabus,
    val fetchSubjectMarkDown: FetchSubjectMarkDown
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