package com.atech.core.use_case

import com.atech.core.data_source.ktor.BitAppApiService
import javax.inject.Inject


class KTorUseCase @Inject constructor(
    val fetchSyllabus: FetchSyllabus
)

data class FetchSyllabus @Inject constructor(
    private val api: BitAppApiService
) {
    suspend operator fun invoke(
        courseWithSem: String
    ) =
        api.getSubjects(
            course = courseWithSem.replace("\\d".toRegex(), ""),
            courseSem = courseWithSem
        )

}