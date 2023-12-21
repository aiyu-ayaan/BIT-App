package com.atech.core.datasource.retrofit.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//----------------------------------- About US -----------------------------------
@Keep
data class AboutUsModel(
    val devs: List<Devs>,
    val managers: List<Devs>,
    val contributors: List<Devs?>?,
)

@Keep
data class Devs(
    val sno: Int,
    val name: String,
    @SerializedName("img_link")
    val imageLink: String,
    val website: String,
    val stackoverflow: String,
    val github: String,
    val linkedin: String,
    val instagram: String,
    val des: String
)

fun List<Devs?>?.toDevsList(): List<Devs> = this?.filterNotNull() ?: emptyList()

fun AboutUsModel.isEmpty(): Boolean = devs.isEmpty() && managers.isEmpty() && contributors.isNullOrEmpty()

fun AboutUsModel.toMap(): Map<String, List<Devs>> = mapOf(
    "Developers" to devs,
    "Contributor" to contributors.toDevsList(),
    "Managers" to managers
)

//----------------------------------- Holiday -----------------------------------

enum class HolidayType(val value: String) {
    MAIN("main"), RES("res"), ALL("ALL")
}

@Keep
class HolidayModel(
    val holidays: List<Holiday>
)

@Keep
data class Holiday(
    val sno: Int,
    val day: String,
    val date: String,
    val occasion: String,
    val month: String,
    val type: String
)


//----------------------------------- Society -----------------------------------
@Keep
data class SocietyModel(
    val societies: List<Society>,
    val ngos: List<Society>
)

@Keep
data class Society(
    val sno: Int,
    val name: String,
    val des: String,
    val ins: String,
    @SerializedName("logo_link")
    val logo: String
)

//----------------------------------- Syllabus -----------------------------------


@Keep
data class SyllabusResponse(val semester: Semester?)

@Keep
data class Semester(
    val id: String,
    val subjects: Subject
)

@Keep
data class Subject(
    val theory: List<SubjectModel>,
    val lab: List<SubjectModel>,
    val pe: List<SubjectModel>
)


@Keep
data class SubjectModel(
    val subjectName: String,
    val code: String,
    val shortName: String,
    val credit: Double,
)


@Keep
data class CourseDetail(
    val courseName: String,
    val totalSemester: Int,
)
