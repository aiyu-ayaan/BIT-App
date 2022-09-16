/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/11/21, 11:11 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/11/21, 9:45 PM
 */



package com.atech.bit.ui.fragments.course.sem_choose.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowSyllabusBinding
import com.atech.core.data.room.syllabus.SyllabusDiffCallback
import com.atech.core.data.room.syllabus.SyllabusModel

class SubjectAdapter(
    private val listener: (syllabusModel: SyllabusModel, view: View) -> Unit
) :
    ListAdapter<SyllabusModel, SubjectAdapter.SearchViewHolder>(SyllabusDiffCallback()) {

    inner class SearchViewHolder(private val binding: RowSyllabusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                binding.root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val model = getItem(position)
                        if (model != null) {
                            listener.invoke(model, binding.root)
                        }
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(syllabusModel: SyllabusModel) {
            binding.apply {
                binding.root.transitionName = syllabusModel.openCode
                subjectName.text = syllabusModel.subject
                code.text = syllabusModel.code
                credits.text = "Credits:${syllabusModel.credits}"
                credits.visibility = if (syllabusModel.credits == -1) View.GONE else View.VISIBLE
                code.visibility = if (syllabusModel.code.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(
            RowSyllabusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}