package com.atech.core.data_source.ktor.model


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

//----------------------------------- Holiday -----------------------------------
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


@kotlinx.serialization.Serializable
data class SyllabusResponse(val semester: Semester?)

@Keep
@kotlinx.serialization.Serializable
data class Semester(
    val id: String,
    val subjects: Subject
)

@Keep
@kotlinx.serialization.Serializable
data class Subject(
    val theory: List<SubjectModel>,
    val lab: List<SubjectModel>,
    val pe: List<SubjectModel>
)


@Keep
@kotlinx.serialization.Serializable
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
