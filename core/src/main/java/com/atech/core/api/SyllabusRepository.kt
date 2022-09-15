package com.atech.core.api

import javax.inject.Inject

class SyllabusRepository @Inject constructor(
    private val api: SyllabusApi
) {
    suspend fun getSyllabus() = api.getSubjects()
}