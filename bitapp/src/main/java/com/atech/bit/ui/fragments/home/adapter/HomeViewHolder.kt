package com.atech.bit.ui.fragments.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atech.attendance.utils.findPercentage
import com.atech.bit.databinding.LayoutHomeTopSettingsBinding
import com.atech.bit.databinding.RowAttendanceRvBinding
import com.atech.bit.databinding.RowCgpaHomeBinding
import com.atech.bit.databinding.RowCommonRvBinding
import com.atech.bit.databinding.RowHolidayHomeBinding
import com.atech.bit.databinding.RowLibraryHomeBinding
import com.atech.bit.databinding.RowSubjectsHomeBinding
import com.atech.bit.utils.AttendanceHomeAdapter
import com.atech.bit.utils.EventAdapter
import com.atech.bit.utils.HomeLibraryAdapter
import com.atech.bit.utils.HomeTopModel
import com.atech.bit.utils.getImageLinkNotification
import com.atech.bit.utils.set
import com.atech.bit.utils.setView
import com.atech.core.room.library.LibraryModel
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import com.atech.theme.databinding.CardViewHighlightBinding
import com.atech.theme.databinding.LayoutNoDataFoundBinding
import com.atech.theme.databinding.LayoutNoteFromDevBinding
import com.atech.theme.databinding.RowAdsViewBinding
import com.atech.theme.databinding.RowNoticeEventBinding
import com.atech.theme.databinding.RowTitleBinding
import com.atech.theme.getDate
import com.atech.theme.loadImage
import com.atech.theme.set
import com.atech.theme.setAdsUnit
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.materialswitch.MaterialSwitch
import java.math.RoundingMode
import java.text.DecimalFormat

sealed class HomeViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    class HighlightHolder(
        private val binding: CardViewHighlightBinding,
        private val onClick: () -> Unit
    ) : HomeViewHolder(binding) {
        fun bind(
            model: CardHighlightModel
        ) {
            binding.set(
                model.copy(onClick = onClick)
            )
        }
    }

    class LibraryHolder(
        private val binding: RowLibraryHomeBinding,
        private val onDeleteClick: (LibraryModel) -> Unit,
        private val onMarkAsReturnClick: (LibraryModel) -> Unit
    ) : HomeViewHolder(binding) {
        fun bind(
            model: HomeItems.Library
        ) {
            binding.carouselRecyclerView.apply {
                val commonAdapter = HomeLibraryAdapter(
                    onDeleteClick,
                    onMarkAsReturnClick
                )
                commonAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                adapter = commonAdapter
                layoutManager = CarouselLayoutManager()
                commonAdapter.submitList(model.data)
            }
        }
    }

    class HomeSettingHolder(
        private val binding: LayoutHomeTopSettingsBinding,
        private val switchClick: (Boolean) -> Unit,
        private val switchApply: MaterialSwitch.() -> Unit,
        private val settingClick: () -> Unit,
        private val editClick: () -> Unit
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
                    }
                }
                setting.setOnClickListener {
                    settingClick()
                }
                edit.setOnClickListener {
                    editClick()
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
        private val binding: RowCommonRvBinding, private val onEventClick: (String) -> Unit
    ) : HomeViewHolder(binding) {
        fun bind(model: HomeItems.Event) {
            val events = model.data
            binding.apply {
                val commonAdapter = EventAdapter(onEventClick)
                commonAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                carouselRecyclerView.apply {
                    adapter = commonAdapter
                    layoutManager = CarouselLayoutManager()
                }
                commonAdapter.items = events
            }
        }
    }

    class CgpaHolder(
        private val binding: RowCgpaHomeBinding
    ) : HomeViewHolder(binding) {
        fun bind(model: HomeItems.Cgpa) {
            binding.lineChartCgpa.setView(
                binding.root.context,
                model.data,
            )
        }
    }


    class AttendanceHolder(
        private val binding: RowAttendanceRvBinding,
        private val defaultPercentage: Int,
    ) : HomeViewHolder(binding) {
        fun bind(model: HomeItems.Attendance) {
            val (total, present, list) = model.data
            val finalPercentage =
                findPercentage(present.toFloat(), total.toFloat()) { present1, total1 ->
                    when (total1) {
                        0.0F -> 0.0F
                        else -> ((present1 / total1) * 100)
                    }
                }
            binding.apply {
                textViewTotal.text = total.toString()
                textViewPresent.text = present.toString()
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                textViewOverAllAttendance.text = binding.root.context.getString(
                    R.string.overallAttendance, df.format(finalPercentage)
                )
                val attendanceAdapter = AttendanceHomeAdapter(defaultPercentage)
                recyclerViewShowAttendance.apply {
                    adapter = attendanceAdapter
                    layoutManager = CarouselLayoutManager()
                }
                attendanceAdapter.submitList(list)
            }
        }
    }

    class NoticeHolder(
        private val binding: RowNoticeEventBinding,
        private val navigateToDetailScreen: (String) -> Unit,
    ) : HomeViewHolder(binding) {
        fun bind(model: HomeItems.Notice) {
            val notice = model.data
            binding.apply {
                bodyPreviewTextView.text = notice.body
                senderTextView.text = notice.sender
                subjectTextView.text = notice.title
                textViewDate.text = notice.created?.getDate()
                senderProfileImageView.loadImage(
                    notice.getImageLinkNotification()
                )
                root.setOnClickListener {
                    navigateToDetailScreen(notice.path!!)
                }
            }
        }
    }

    class NoDataHolder(
        binding: LayoutNoDataFoundBinding
    ) : HomeViewHolder(binding)

    class DevNoteHolder(
        binding: LayoutNoteFromDevBinding
    ) : HomeViewHolder(binding)

    class AdsViewHolder(
        private val binding: RowAdsViewBinding
    ) : HomeViewHolder(binding) {
        fun bind(adsUnit: HomeItems.Ads) {
            binding.setAdsUnit(
                adsUnit.ads
            )
        }
    }
}