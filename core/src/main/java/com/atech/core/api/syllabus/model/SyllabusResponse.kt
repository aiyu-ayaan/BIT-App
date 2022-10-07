package com.atech.core.api.syllabus.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SyllabusResponse( val semesters: Semesters) : Parcelable

@Keep
@Parcelize
@Entity(tableName = "syllabus_cached_table")
data class Semesters(
    @PrimaryKey
    val id: String,
    @Embedded val subjects: Subject
) : Parcelable

@Parcelize
@Keep
data class Subject(
    val theory: List<Theory>,
    val lab: List<Lab>?
) : Parcelable


object TheoryListTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toString(value: String): List<Theory> =
        Gson().fromJson(value, object : TypeToken<List<Theory>>() {}.type)


    @TypeConverter
    @JvmStatic
    fun toArrayList(list: List<Theory>): String = Gson().toJson(list)
}

object LabListTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toString(value: String): List<Lab> =
        Gson().fromJson(value, object : TypeToken<List<Lab>>() {}.type)


    @TypeConverter
    @JvmStatic
    fun toArrayList(list: List<Lab>): String = Gson().toJson(list)
}

@Keep
@Parcelize
data class Theory(
    val subjectName: String,
    var type: String,
    val code: String,
    val openCode: String,
    val shortName: String,
    val credit: Int,
    val listOrder: Int,
    val content: List<TheoryContent>,
    @Embedded val books: Books
) : Parcelable

object TheoryContentTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toString(value: String): List<TheoryContent> =
        Gson().fromJson(value, object : TypeToken<List<TheoryContent>>() {}.type)


    @TypeConverter
    @JvmStatic
    fun toArrayList(list: List<TheoryContent>): String = Gson().toJson(list)
}


@Keep
@Parcelize
data class TheoryContent(
    val module: String,
    val content: String,
) : Parcelable

@Keep
@Parcelize
data class Lab(
    val subjectName: String,
    var type: String,
    val code: String,
    val openCode: String,
    val shortName: String,
    val credit: Int,
    val listOrder: Int,
    val content: List<LabContent>,
    @Embedded val books: Books
) : Parcelable

object LabContentTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toString(value: String): List<LabContent> =
        Gson().fromJson(value, object : TypeToken<List<LabContent>>() {}.type)


    @TypeConverter
    @JvmStatic
    fun toArrayList(list: List<LabContent>): String = Gson().toJson(list)
}


@Keep
@Parcelize
data class LabContent(
    val question: String,
    val image: String?
) : Parcelable

@Keep
@Parcelize
data class Books(
    val textBooks: List<BookName>,
    val referenceBooks: List<BookName>
) : Parcelable


object BooksNameTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toString(value: String): List<BookName> =
        Gson().fromJson(value, object : TypeToken<List<BookName>>() {}.type)


    @TypeConverter
    @JvmStatic
    fun toArrayList(list: List<BookName>): String = Gson().toJson(list)
}


@Keep
@Parcelize
data class BookName(
    val bookName: String
) : Parcelable

class DiffUtilTheorySyllabusCallback() : DiffUtil.ItemCallback<Theory>() {
    override fun areItemsTheSame(oldItem: Theory, newItem: Theory): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Theory, newItem: Theory): Boolean {
        return oldItem == newItem
    }
}

class DiffUtilLabSyllabusCallback() : DiffUtil.ItemCallback<Lab>() {
    override fun areItemsTheSame(oldItem: Lab, newItem: Lab): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Lab, newItem: Lab): Boolean {
        return oldItem == newItem
    }
}