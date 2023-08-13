package com.atech.bit.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.core.firebase.firestore.Attach
import com.atech.theme.databinding.RowNoticeImagePreviewBinding
import com.atech.theme.loadImage

class ImagePreviewAdapter(
    private val listener: (String) -> Unit = {}
) :
    RecyclerView.Adapter<ImagePreviewAdapter.ImagePreviewViewHolder>() {

    var items = mutableListOf<Attach>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ImagePreviewViewHolder(
        private val binding: RowNoticeImagePreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    listener.invoke(
                        items[pos].link!!
                    )
            }
        }

        fun bind(attach: Attach) {
            binding.apply {
                binding.root.transitionName = attach.link
                binding.root.loadImage(
                    attach.link
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

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
        holder.bind(
            items[position]
        )
    }
}