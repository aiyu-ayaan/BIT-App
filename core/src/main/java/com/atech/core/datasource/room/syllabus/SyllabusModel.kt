/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:22 AM
 */



package com.atech.core.datasource.room.syllabus

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

enum class SubjectType(val value: String) {
    THEORY("Theory"),
    LAB("Lab"),
    PE("PE")

}

@Keep
@Entity(tableName = "syllabus_table")
@Parcelize
data class SyllabusModel(
    val subject: String,
    val code: String = "",
    val credits: Int = -1,
    @PrimaryKey(autoGenerate = false)
    val openCode: String,
    val type: String = "THEORY",
    val group: String = "",
    val shortName: String,
    val listOrder: Int,
    @Embedded
    val subjectContent: Subject? = null,
    val isChecked: Boolean? = true,
    val isAdded: Boolean? = false,
    val fromNetwork: Boolean? = false,
    val deprecated: Boolean? = false
) : Parcelable, Serializable

@Keep
@Parcelize
data class Subject(
    val content: String?,
    val book: String?,
    val reference: String?
) : Parcelable, Serializable


class SyllabusDiffCallback : DiffUtil.ItemCallback<SyllabusModel>() {
    override fun areItemsTheSame(oldItem: SyllabusModel, newItem: SyllabusModel): Boolean =
        oldItem.openCode == newItem.openCode

    override fun areContentsTheSame(oldItem: SyllabusModel, newItem: SyllabusModel): Boolean =
        oldItem == newItem
}