/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/29/21, 12:32 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/29/21, 12:32 AM
 */

package com.aatec.bit.fragments.about_us.acknowledgement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.databinding.RowComponentBinding
import com.aatec.core.data.ui.componentUse.Component
import com.aatec.core.data.ui.componentUse.ComponentUseDiffUtil

class AcknowledgementAdapter(
    private val listener: (String) -> Unit
) :
    ListAdapter<Component, AcknowledgementAdapter.ComponentUseViewHolder>(ComponentUseDiffUtil()) {
    inner class ComponentUseViewHolder constructor(
        private val binding: RowComponentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.invoke(getItem(position).link)
                }
            }
        }

        fun bind(com: Component) = binding.apply {
            textViewName.text = com.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentUseViewHolder =
        ComponentUseViewHolder(
            RowComponentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ComponentUseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}