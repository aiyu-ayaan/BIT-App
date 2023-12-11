package com.atech.core.data_source.ktor

sealed class BITAppApiRoute(val route: String) {
    data class Subject(val course: String, val courseSem: String) :
        BITAppApiRoute("${BASE_URL}/syllabus/$course/$courseSem/$courseSem.json")

    data class SubjectMarkdown(val course: String, val courseSem: String, val subject: String) :
        BITAppApiRoute("${BASE_URL}/syllabus/$course/$courseSem/subjects/$subject.md")

    companion object {
        const val BASE_URL = "https://bit-lalpur-app.github.io/BIT-App-Data/data/"
    }
}