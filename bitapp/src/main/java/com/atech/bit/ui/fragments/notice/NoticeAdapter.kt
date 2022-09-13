/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/30/22, 9:33 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/30/22, 9:24 PM
 */

package com.atech.bit.ui.fragments.notice

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowNotice3Binding
import com.atech.core.data.network.notice.Attach
import com.atech.core.data.ui.notice.DiffUtilNotice3
import com.atech.core.data.ui.notice.Notice3
import com.atech.core.utils.DEFAULT_CORNER_RADIUS
import com.atech.core.utils.convertLongToTime
import com.atech.core.utils.getImageLinkNotification
import com.atech.core.utils.loadImage
import com.google.firebase.firestore.FirebaseFirestore

class NoticeAdapter(
    private val db: FirebaseFirestore,
    private val listener: (Notice3, View) -> Unit,
    private val imageListener: (String) -> Unit
) : ListAdapter<Notice3, NoticeAdapter.AdapterViewHolder>(DiffUtilNotice3()) {


    inner class AdapterViewHolder(
        private val binding: RowNotice3Binding
    ) : RecyclerView.ViewHolder(binding.root) {
        var attach: MutableList<Attach>? = null

        init {
            binding.materialCardViewNotice.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener.invoke(getItem(position), binding.root)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(notice: Notice3) {
            binding.apply {
                binding.root.transitionName = notice.title
                subjectTextView.text = notice.title
                bodyPreviewTextView.text = notice.body.replace("<br/>", "\n")
                val imageAdapter = ImagePreviewAdapter { link ->
                    imageListener.invoke(link)
                }
                attachmentRecyclerView.apply {
                    adapter = imageAdapter
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
                senderTextView.text = binding.root.context.resources.getString(
                    R.string.notice_sender,
                    notice.sender
                )
                textViewDate.text =
                    binding.root.context.resources.getString(
                        R.string.notice_date,
                        notice.created.convertLongToTime("dd/MM/yyyy")
                    )
                notice.getImageLinkNotification().loadImage(
                    binding.root,
                    binding.senderProfileImageView,
                    binding.progressBarNoticePreview,
                    DEFAULT_CORNER_RADIUS,
                    R.drawable.ic_running_error
                )
                db.collection("BIT_Notice_New").document(notice.path).collection("attach")
                    .addSnapshotListener { value, _ ->
                        try {
                            attach = value?.toObjects(Attach::class.java)
                            attach?.let {
                                attachmentRecyclerView.isVisible = it.isNotEmpty()
                                imageAdapter.submitList(it)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                binding.root.context,
                                "${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder =
        AdapterViewHolder(
            RowNotice3Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}