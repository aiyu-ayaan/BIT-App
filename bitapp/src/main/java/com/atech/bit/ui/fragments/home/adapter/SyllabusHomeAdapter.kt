/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/13/22, 10:50 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/13/22, 8:21 PM
 */



package com.atech.bit.ui.fragments.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowSyllabusHomeBinding
import com.atech.core.data.room.syllabus.SyllabusDiffCallback
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.utils.REQUEST_ADAPTER_EDIT
import com.atech.core.utils.REQUEST_ADAPTER_SEARCH
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS

class SyllabusHomeAdapter(
    private val listener: ((syllabusModel: SyllabusModel, view: View) -> Unit)? = null,
    private val request: Int = 0,
    private val clickListener: ((CheckBox, SyllabusModel) -> Unit)? = null,
    private val editListener: ((SyllabusModel) -> Unit)? = null
) :
    ListAdapter<SyllabusModel, SyllabusHomeAdapter.SyllabusViewHolder>(SyllabusDiffCallback()) {

    inner class SyllabusViewHolder(
        private val binding: RowSyllabusHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(absoluteAdapterPosition)
                    listener?.invoke(item, binding.root)
                    clickListener?.invoke(binding.checkBox, item)
                }
            }
            binding.ibEdit.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(absoluteAdapterPosition)
                    editListener?.invoke(item)
                }
            }
        }

        fun bind(syllabusModel: SyllabusModel) {
            binding.apply {
                binding.root.transitionName = syllabusModel.openCode
                tvSem.isVisible = request == REQUEST_ADAPTER_SEARCH
                checkBox.apply {
                    isVisible =
                        request == REQUEST_ADAPTER_EDIT || request == REQUEST_ADD_SUBJECT_FROM_SYLLABUS
                    isClickable = false
                    isChecked = when (request) {
                        REQUEST_ADD_SUBJECT_FROM_SYLLABUS -> syllabusModel.isAdded ?: false
                        else -> syllabusModel.isChecked ?: false
                    }
                    ibEdit.isVisible = isChecked && request == REQUEST_ADD_SUBJECT_FROM_SYLLABUS
                }
                tvSubjectName.text = syllabusModel.subject
                tvSubjectCode.text = syllabusModel.code
                tvSem.text =
                    if (syllabusModel.openCode.length == 6) syllabusModel.openCode.dropLast(2)
                    else syllabusModel.openCode.dropLast(1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder =
        SyllabusViewHolder(
            RowSyllabusHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}