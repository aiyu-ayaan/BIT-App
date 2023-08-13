package com.atech.core.retrofit.client

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

//----------------------------------- About US -----------------------------------
@Keep
data class AboutUsModel(
    val devs: List<Devs>,
    val managers: List<Devs>,
    val contributors: List<Devs?>?,
)

@Keep
@Parcelize
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
) : Parcelable

//----------------------------------- Holiday -----------------------------------
@Keep
class HolidayModel(
    val holidays: List<Holiday>
)

@Keep
@Parcelize
data class Holiday(
    val sno: Int,
    val day: String,
    val date: String,
    val occasion: String,
    val month: String,
    val type: String
) : Parcelable, Serializable


class DiffCallbackHoliday : DiffUtil.ItemCallback<Holiday>() {
    override fun areItemsTheSame(oldItem: Holiday, newItem: Holiday): Boolean =
        oldItem.occasion == newItem.occasion

    override fun areContentsTheSame(oldItem: Holiday, newItem: Holiday): Boolean =
        oldItem == newItem
}

//----------------------------------- Society -----------------------------------
@Keep
data class SocietyModel(
    val societies: List<Society>,
    val ngos: List<Society>
)

@Keep
@Parcelize
data class Society(
    val sno: Int,
    val name: String,
    val des: String,
    val ins: String,
    @SerializedName("logo_link")
    val logo: String
) : Parcelable

//----------------------------------- Syllabus -----------------------------------

@Keep
@Parcelize
data class SyllabusResponse(val semester: Semester?) : Parcelable

@Keep
@Parcelize
data class Semester(
    val id: String,
    val subjects: Subject
) : Parcelable

@Parcelize
@Keep
data class Subject(
    val theory: List<SubjectModel>,
    val lab: List<SubjectModel>,
    val pe: List<SubjectModel>
) : Parcelable


@Keep
@Parcelize
data class SubjectModel(
    val subjectName: String,
    val code: String,
    val shortName: String,
    val credit: Double,
) : Parcelable


class DiffUtilTheorySyllabusCallback : DiffUtil.ItemCallback<SubjectModel>() {
    override fun areItemsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
        return oldItem == newItem
    }
}


@Keep
data class CourseDetail(
    val courseName: String,
    val totalSemester: Int,
)
