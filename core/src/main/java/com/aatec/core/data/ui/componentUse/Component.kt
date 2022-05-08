/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:22 AM
 */

package com.aatec.core.data.ui.componentUse

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Component(
    val name: String,
    val link: String
) : Parcelable

class ComponentUseDiffUtil : DiffUtil.ItemCallback<Component>() {
    override fun areItemsTheSame(oldItem: Component, newItem: Component): Boolean =
        newItem == oldItem

    override fun areContentsTheSame(oldItem: Component, newItem: Component): Boolean =
        newItem == oldItem

}