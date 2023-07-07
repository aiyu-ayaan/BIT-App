package com.atech.bit.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowCarouselBinding
import com.atech.bit.ui.fragments.home.HomeViewModelExr
import com.atech.theme.loadImage


class EventAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    var items: List<HomeViewModelExr.EventHomeModel> = emptyList()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class EventViewHolder(
        private val binding: RowCarouselBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION)
                    onClick(items[absoluteAdapterPosition].path)
            }
            binding.root.setOnMaskChangedListener { maskRect ->
                binding.carouselTextView.translationX = maskRect.left;
                binding.carouselTextView.alpha = 1 - maskRect.left / 100f
            }
        }

        fun bind(model: HomeViewModelExr.EventHomeModel) {
            binding.apply {
                carouselTextView.text = model.title
                carouselImageView.loadImage(model.posterLink.ifBlank { model.iconLink })
                carouselImageView.loadImage(model.posterLink.ifBlank { model.iconLink }) {
                    it?.let { bitmap ->
                        bitmap.isDark { isDark ->
                            if (isDark)
                                carouselTextView.setTextColor(0xFFFFFFFF.toInt())
                            else
                                carouselTextView.setTextColor(0xFF000000.toInt())
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(
            RowCarouselBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
        holder.bind(items[position])
}
