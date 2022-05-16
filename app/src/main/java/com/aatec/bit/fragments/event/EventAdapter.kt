/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/30/22, 9:33 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/29/22, 10:03 PM
 */



package com.aatec.bit.fragments.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.R
import com.aatec.bit.databinding.RowEventBinding
import com.aatec.bit.utils.loadImage
import com.aatec.core.data.ui.event.Event
import com.aatec.core.data.ui.event.EventDiffUtil
import com.aatec.core.utils.DEFAULT_CORNER_RADIUS

class EventAdapter(
    private val listener: ((Event) -> Unit)?
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffUtil()) {

    inner class EventViewHolder constructor(
        private val binding: RowEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.invoke(getItem(position))
                }
            }
        }

        fun bind(event: Event) {
            binding.apply {
                binding.root.transitionName = event.event_title
                textViewAbout.text = event.event_title
                dateEvent.text = event.date
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
                    24,
                    R.drawable.ic_running_error
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(
            RowEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface ClickListener {
        fun setOnClickListener(event: Event, view: View)
    }
}