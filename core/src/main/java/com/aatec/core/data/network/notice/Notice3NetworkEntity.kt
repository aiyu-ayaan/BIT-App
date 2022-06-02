/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/13/22, 10:35 AM
 */

package com.aatec.core.data.network.notice

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Keep
class Notice3NetworkEntity {
    var title = ""
    var body = ""
    var link = ""
    var sender = ""
    var path = ""
    var created: Long = 0L
}

@Keep
@Parcelize
data class Attach(
    var added: Long? = null,
    var link: String? = null
) : Parcelable

class DiffUtilAttach() : DiffUtil.ItemCallback<Attach>() {
    override fun areItemsTheSame(oldItem: Attach, newItem: Attach): Boolean =
        oldItem.link == newItem.link

    override fun areContentsTheSame(oldItem: Attach, newItem: Attach): Boolean =
        oldItem.link == newItem.link

}