/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/11/21, 9:02 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/11/21, 8:59 PM
 */



package com.atech.bit.ui.fragments.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R

import com.atech.bit.databinding.RowEventHomeBinding
import com.atech.core.data.ui.event.Event
import com.atech.core.data.ui.event.EventDiffUtil
import com.atech.core.utils.DEFAULT_CORNER_RADIUS
import com.atech.core.utils.convertLongToTime
import com.atech.core.utils.loadImage

class EventHomeAdapter(
    private val listener: ((Event, View) -> Unit)?
) : ListAdapter<Event, EventHomeAdapter.EventViewHolder>(EventDiffUtil()) {

    inner class EventViewHolder constructor(
        private val binding: RowEventHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener?.invoke(getItem(position), binding.root)
            }
        }

        fun bind(event: Event) {
            binding.apply {
                binding.root.transitionName = event.event_title
                textViewAbout.text = event.event_title
                dateEvent.text = event.created.convertLongToTime("dd MMMM yyyy")
                event.logo_link.loadImage(
                    itemView,
                    eventImg,
                    progressBarEvent,
                    DEFAULT_CORNER_RADIUS,
                    R.drawable.ic_running_error
                )
                event.poster_link.loadImage(
                    itemView,
                    posterEvent,
                    progressBarPoster,
                    50,
                    R.drawable.ic_running_error
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(
            RowEventHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}