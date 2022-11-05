package com.atech.core.api.holiday

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize
import java.io.Serializable

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

