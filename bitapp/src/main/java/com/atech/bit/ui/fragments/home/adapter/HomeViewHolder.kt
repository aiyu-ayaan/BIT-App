package com.atech.bit.ui.fragments.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atech.bit.databinding.LayoutHomeTopSettingsBinding
import com.atech.bit.databinding.RowCommonRvBinding
import com.atech.bit.databinding.RowHolidayHomeBinding
import com.atech.bit.databinding.RowSubjectsHomeBinding
import com.atech.bit.utils.EventAdapter
import com.atech.bit.utils.HomeTopModel
import com.atech.bit.utils.set
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import com.atech.theme.databinding.CardViewHighlightBinding
import com.atech.theme.databinding.LayoutNoteFromDevBinding
import com.atech.theme.databinding.RowTitleBinding
import com.atech.theme.set
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.materialswitch.MaterialSwitch

sealed class HomeViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    class HighlightHolder(
        private val binding: CardViewHighlightBinding
    ) : HomeViewHolder(binding) {
        fun bind(
            model: CardHighlightModel
        ) {
            binding.set(
                model
            )
        }
    }

    class HomeSettingHolder(
        private val binding: LayoutHomeTopSettingsBinding,
        private val switchClick: (Boolean) -> Unit,
        private val switchApply: MaterialSwitch.() -> Unit
    ) : HomeViewHolder(binding) {
        fun bind(
            model: HomeTopModel
        ) {
            binding.apply {
                set(model)
                toggleSwitch.apply {
                    switchApply(this)
                    setOnCheckedChangeListener { _, isChecked ->
                        switchClick(isChecked)
                        if (isChecked)
                            setThumbIconResource(R.drawable.round_cloud_24)
                        else
                            setThumbIconResource(R.drawable.round_cloud_off_24)
                    }
                }
            }
        }
    }

    class TitleHolder(
        private val binding: RowTitleBinding,
    ) : HomeViewHolder(binding) {
        fun bind(title: HomeItems.Title) {
            binding.root.text = title.title
        }
    }

    class SubjectHolder(
        private val binding: RowSubjectsHomeBinding, onClick: ((Int) -> Unit)? = null
    ) : HomeViewHolder(binding) {
        init {
            binding.root.setOnClickListener {
                onClick?.invoke(absoluteAdapterPosition)
            }
        }

        fun bind(model: HomeItems.Subject) {
            binding.apply {
                binding.root.transitionName = model.data.subject
                subjectTextView.text = model.data.subject
                creditTextView.text = String.format("Credits: %d", model.data.credits)
                creditTextView.visibility =
                    if (model.data.credits == -1) View.GONE else View.VISIBLE
                subjectCodeTextView.text = model.data.code
                subjectCodeTextView.visibility =
                    if (model.data.code.isEmpty()) View.GONE else View.VISIBLE

            }
        }
    }

    class HolidayHolder(
        private val binding: RowHolidayHomeBinding
    ) : HomeViewHolder(binding) {
        fun bind(model: HomeItems.Holiday) {
            val holiday = model.data
            binding.apply {
                date.text = holiday.date
                day.text = holiday.day
                occasion.text = holiday.occasion
            }
        }
    }

    class EventHolder(
        private val binding: RowCommonRvBinding
    ) : HomeViewHolder(binding) {
        fun bind(model: HomeItems.Event) {
            val events = model.data
            binding.apply {
                val commonAdapter = EventAdapter()
                carouselRecyclerView.apply {
                    adapter = commonAdapter
                    layoutManager = CarouselLayoutManager()
                }
                commonAdapter.items = events
            }
        }
    }

    class DevNoteHolder(
        binding: LayoutNoteFromDevBinding
    ) : HomeViewHolder(binding)
}