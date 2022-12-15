package com.atech.core.data.room.library

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.atech.core.utils.DEFAULT_DATE_FORMAT
import com.atech.core.utils.convertLongToTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Keep
@Entity(tableName = "library_table")
@Parcelize
data class LibraryModel(
    val bookName: String,
    val bookId: String,
    val issueDate: Long,
    val returnDate: Long,
    val alertDate: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : Parcelable {
    @get:Ignore
    @IgnoredOnParcel
    val issueFormatData: String
        get() = issueDate.convertLongToTime(DEFAULT_DATE_FORMAT)

    @get:Ignore
    @IgnoredOnParcel
    val returnFormatData: String
        get() = returnDate.convertLongToTime(DEFAULT_DATE_FORMAT)

    @get:Ignore
    @IgnoredOnParcel
    val alertFormatData: String
        get() = alertDate.convertLongToTime(DEFAULT_DATE_FORMAT)
}