package com.atech.core.data_source.room.library

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.atech.core.utils.DD_MM
import com.atech.core.utils.convertLongToTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Keep
@Entity(tableName = "library_table")
@Parcelize
data class LibraryModel(
    val bookName: String = "",
    val bookId: String = "",
    val issueDate: Long = 0L,
    val returnDate: Long = 0L,
    val alertDate: Long = 0L,
    val eventId: Long = -1L,
    val markAsReturn: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : Parcelable {
    @get:Ignore
    @IgnoredOnParcel
    val issueFormatData: String
        get() = issueDate.convertLongToTime(DD_MM)

    @get:Ignore
    @IgnoredOnParcel
    val returnFormatData: String
        get() = returnDate.convertLongToTime(DD_MM)

    @get:Ignore
    @IgnoredOnParcel
    val alertFormatData: String
        get() = alertDate.convertLongToTime(DD_MM)
}

class DiffUtilCallbackLibrary : DiffUtil.ItemCallback<LibraryModel>() {
    override fun areItemsTheSame(oldItem: LibraryModel, newItem: LibraryModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryModel, newItem: LibraryModel): Boolean {
        return oldItem == newItem
    }
}