/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/14/22, 10:50 AM
 */

package com.aatec.bit.ui.fragments.notice

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.R
import com.aatec.bit.databinding.RowNoticeImageGridBinding
import com.aatec.core.data.network.notice.Attach
import com.aatec.core.utils.MAX_SPAWN
import com.aatec.core.utils.loadImageDefault
import kotlin.random.Random

class ImageGridAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<ImageGridAdapter.ImageGridViewHolder>() {

    private var list: List<Attach> = emptyList()

    inner class ImageGridViewHolder(
        private val binding: RowNoticeImageGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    listener.invoke(list[pos].link!!)
            }
        }

        fun bind(attach: Attach) {
            binding.apply {
                binding.root.transitionName = attach.link
                attach.link!!.loadImageDefault(
                    binding.root,
                    attachmentImageView,
                    progressBarImageGrid,
                    R.drawable.ic_running_error
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGridViewHolder =
        ImageGridViewHolder(
            RowNoticeImageGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(attachments: List<Attach>) {
        list = attachments
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ImageGridViewHolder, position: Int) {
        holder.bind(list[position])
    }

    val variableSpanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

        private var indexSpanCounts: List<Int> = emptyList()

        override fun getSpanSize(position: Int): Int {
            return indexSpanCounts[position]
        }

        private fun generateSpanCountForItems(count: Int): List<Int> {
            val list = mutableListOf<Int>()

            var rowSpansOccupied = 0
            repeat(count) {
                val size = Random.nextInt(1, MAX_SPAWN + 1 - rowSpansOccupied)
                rowSpansOccupied += size
                if (rowSpansOccupied >= 3) rowSpansOccupied = 0
                list.add(size)
            }

            return list
        }

        override fun invalidateSpanIndexCache() {
            super.invalidateSpanIndexCache()
            indexSpanCounts = generateSpanCountForItems(itemCount)
        }
    }

    override fun getItemCount(): Int =
        list.size
}