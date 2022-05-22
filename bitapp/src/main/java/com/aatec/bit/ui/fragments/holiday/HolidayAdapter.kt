/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/5/22, 12:03 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/5/22, 12:02 PM
 */



package com.aatec.bit.ui.fragments.holiday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.databinding.RowHolidayBinding
import com.aatec.core.data.ui.holiday.DiffCallbackHoliday
import com.aatec.core.data.ui.holiday.Holiday

class HolidayAdapter :
    ListAdapter<Holiday, HolidayAdapter.HolidayViewHolder>(DiffCallbackHoliday()) {

    class HolidayViewHolder(private val binding: RowHolidayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }

        fun bind(holiday: Holiday) {
            binding.apply {
                date.text = holiday.date
                day.text = holiday.day
                occasion.text = holiday.occasion
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder =
        HolidayViewHolder(
            RowHolidayBinding.inflate(
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