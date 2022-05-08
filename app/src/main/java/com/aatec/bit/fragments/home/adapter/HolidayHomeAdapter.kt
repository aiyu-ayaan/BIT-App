/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/11/21, 9:02 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/11/21, 8:59 PM
 */



package com.aatec.bit.fragments.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.databinding.RowHolidayHomeBinding
import com.aatec.core.data.ui.holiday.DiffCallbackHoliday
import com.aatec.core.data.ui.holiday.Holiday

class HolidayHomeAdapter(
    private val clickListener: (() -> Unit)? = null
) :
    ListAdapter<Holiday, HolidayHomeAdapter.HolidayViewHolder>(DiffCallbackHoliday()) {

    inner class HolidayViewHolder(private val binding: RowHolidayHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener?.invoke()
                }
            }
        }

        fun bind(holiday: Holiday) {
            binding.apply {
                date.text = holiday.date
                day.text = holiday.day
                occasion.text = holiday.occasion
                type.text = holiday.type
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder =
        HolidayViewHolder(
            RowHolidayHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }


}