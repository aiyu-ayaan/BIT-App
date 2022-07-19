/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/14/22, 10:50 AM
 */

package com.atech.bit.ui.fragments.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.core.data.network.notice.Attach
import com.atech.core.data.network.notice.DiffUtilAttach
import com.atech.bit.databinding.RowNoticeImagePreviewBinding
import com.atech.core.utils.loadImageDefault

class ImagePreviewAdapter(
    private val listener: (String) -> Unit
) :
    ListAdapter<Attach, ImagePreviewAdapter.ImagePreviewViewHolder>(DiffUtilAttach()) {

    inner class ImagePreviewViewHolder(
        private val binding: RowNoticeImagePreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    listener.invoke(getItem(pos).link!!)
            }
        }

        fun bind(attach: Attach) {
            binding.apply {
                binding.root.transitionName = attach.link
                attach.link!!.loadImageDefault(
                    binding.root,
                    attachmentImageView,
                    progressBarImagePreview,
                    R.drawable.ic_running_error
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder =
        ImagePreviewViewHolder(
            RowNoticeImagePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}