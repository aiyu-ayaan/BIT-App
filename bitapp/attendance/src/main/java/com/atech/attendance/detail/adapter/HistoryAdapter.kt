/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.attendance.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.attendance.databinding.RowCalendarViewBinding
import com.atech.theme.R
import com.atech.core.room.attendance.IsPresent
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter :
    ListAdapter<IsPresent, HistoryAdapter.HistoryViewHolder>(DiffUtilIsPresent()) {

    class HistoryViewHolder(
        private val binding: RowCalendarViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(isPresent: IsPresent) {
            binding.apply {
                val sf = SimpleDateFormat("dd-MMMM", Locale.getDefault())
                textViewTime.text = sf.format(isPresent.day)
                textViewTotalClasses.text = when (isPresent.totalClasses) {
                    null, 1 -> ""
                    else -> "${isPresent.totalClasses}"
                }
                textViewStatus.text = setTextView(textViewDot, textViewStatus, isPresent)
            }
        }

        private fun setTextView(
            textViewDot: TextView,
            textViewStatus: TextView,
            isPresent: IsPresent
        ): CharSequence =
            when {
                isPresent.isPresent -> {
                    textViewDot.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.green
                        )
                    )
                    textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.green
                        )
                    )
                    "Present"
                }
                else -> {
                    textViewDot.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.red
                        )
                    )
                    textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.red
                        )
                    )
                    "Absent"
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            RowCalendarViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtilIsPresent : DiffUtil.ItemCallback<IsPresent>() {
        override fun areItemsTheSame(oldItem: IsPresent, newItem: IsPresent): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: IsPresent, newItem: IsPresent): Boolean =
            oldItem == newItem

    }
}