package com.atech.core.api.model

import androidx.annotation.Keep

@Keep
data class SyllabusResponse(val semesters: List<Semesters>)

@Keep
data class Semesters(
    val sem: Int,
    val subjects: Subject
)

@Keep
data class Subject(
    val theory: List<Theory>,
    val lab: List<Lab>
)

@Keep
data class Theory(
    val subjectName: String,
    val code: String,
    val openCode: String,
    val shortName: String,
    val credit: Int,
    val listOrder: Int,
    val content: List<TheoryContent>,
    val books: Books
)

@Keep
data class TheoryContent(
    val module: String,
    val content: String,
)

@Keep
data class Lab(
    val subjectName: String,
    val code: String,
    val openCode: String,
    val shortName: String,
    val credit: Int,
    val listOrder: Int,
    val content: List<LabContent>,
    val books: Books
)

@Keep
data class LabContent(
    val question: String,
    val image: String?
)

@Keep
data class Books(
    val textBooks: List<BookName>,
    val referenceBook: List<BookName>
)

@Keep
data class BookName(
    val bookName: String
)