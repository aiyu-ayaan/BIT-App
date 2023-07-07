package com.atech.bit.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowCarouselBinding
import com.atech.bit.ui.fragments.home.HomeViewModelExr
import com.atech.theme.getBitMap
import com.atech.theme.loadImage
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    var items: List<HomeViewModelExr.EventHomeModel> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class EventViewHolder(
        private val binding: RowCarouselBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnMaskChangedListener { maskRect ->
                binding.carouselTextView.translationX = maskRect.left;
                binding.carouselTextView.alpha = 1 - maskRect.left / 100f
            }
        }

        fun bind(model: HomeViewModelExr.EventHomeModel) {
            binding.apply {
                carouselTextView.text = model.title
                carouselImageView.loadImage(
                    model.posterLink.ifBlank { model.iconLink }
                )
                runBlocking {
                    val bitmapDeffer = async {
                        model.posterLink.ifBlank { model.iconLink }
                            .getBitMap(
                                binding.root.context
                            )
                    }
                    val bitmap = bitmapDeffer.await()
                    bitmap?.isDark { isDark ->
                        carouselTextView.setTextColor(
                            if (isDark) {
                                binding.root.context.getColor(android.R.color.white)
                            } else {
                                binding.root.context.getColor(android.R.color.black)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(
            RowCarouselBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
        holder.bind(items[position])
}
