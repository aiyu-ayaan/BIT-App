package com.atech.bit.ui.fragments.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.LayoutHomeTopSettingsBinding
import com.atech.bit.databinding.RowAttendanceRvBinding
import com.atech.bit.databinding.RowCgpaHomeBinding
import com.atech.bit.databinding.RowCommonRvBinding
import com.atech.bit.databinding.RowHolidayHomeBinding
import com.atech.bit.databinding.RowLibraryHomeBinding
import com.atech.bit.databinding.RowSubjectsHomeBinding
import com.atech.core.room.library.LibraryModel
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.databinding.CardViewHighlightBinding
import com.google.android.material.materialswitch.MaterialSwitch

class HomeAdapter(
    private val onSettingClick: () -> Unit = {},
    private val onEditClick: () -> Unit = {},
    private val switchClick: (Boolean) -> Unit = {},
    private val switch: MaterialSwitch.() -> Unit = {},
    private val onEventClick: (String) -> Unit = {},
    private val onSubjectClick: ((SyllabusUIModel) -> Unit) = { },
    private val onDeleteClick: (LibraryModel) -> Unit = {},
    private val onMarkAsReturnClick: (LibraryModel) -> Unit = { },
) : RecyclerView.Adapter<HomeViewHolder>() {

    var items = mutableListOf<HomeItems>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    var defPercentage: Int = 0
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder =
        when (viewType) {
            com.atech.theme.R.layout.card_view_highlight -> HomeViewHolder.HighlightHolder(
                CardViewHighlightBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_library_home -> HomeViewHolder.LibraryHolder(
                RowLibraryHomeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onDeleteClick,
                onMarkAsReturnClick
            )

            R.layout.layout_home_top_settings -> HomeViewHolder.HomeSettingHolder(
                LayoutHomeTopSettingsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                switchClick,
                switch,
                onSettingClick,
                onEditClick
            )

            R.layout.row_subjects_home -> HomeViewHolder.SubjectHolder(
                RowSubjectsHomeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) { position ->
                onSubjectClick.invoke((items[position] as HomeItems.Subject).data)
            }

            com.atech.theme.R.layout.row_title -> HomeViewHolder.TitleHolder(
                com.atech.theme.databinding.RowTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            com.atech.theme.R.layout.layout_note_from_dev -> HomeViewHolder.DevNoteHolder(
                com.atech.theme.databinding.LayoutNoteFromDevBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_holiday_home -> HomeViewHolder.HolidayHolder(
                RowHolidayHomeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_common_rv -> HomeViewHolder.EventHolder(
                RowCommonRvBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onEventClick
            )

            R.layout.row_cgpa_home -> HomeViewHolder.CgpaHolder(
                RowCgpaHomeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_attendance_rv -> HomeViewHolder.AttendanceHolder(
                RowAttendanceRvBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), defPercentage
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) = when (holder) {
        is HomeViewHolder.HighlightHolder -> holder.bind(
            (items[position] as HomeItems.Highlight).model
        )

        is HomeViewHolder.LibraryHolder -> holder.bind(items[position] as HomeItems.Library)

        is HomeViewHolder.HomeSettingHolder -> holder.bind(
            (items[position] as HomeItems.Settings).model
        )

        is HomeViewHolder.TitleHolder -> holder.bind(items[position] as HomeItems.Title)
        is HomeViewHolder.SubjectHolder -> holder.bind(items[position] as HomeItems.Subject)
        is HomeViewHolder.HolidayHolder -> holder.bind(items[position] as HomeItems.Holiday)
        is HomeViewHolder.EventHolder -> holder.bind(items[position] as HomeItems.Event)
        is HomeViewHolder.CgpaHolder -> holder.bind(items[position] as HomeItems.Cgpa)
        is HomeViewHolder.AttendanceHolder -> holder.bind(items[position] as HomeItems.Attendance)
        is HomeViewHolder.DevNoteHolder -> Unit
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is HomeItems.Highlight -> com.atech.theme.R.layout.card_view_highlight
        is HomeItems.Library -> R.layout.row_library_home
        is HomeItems.Settings -> R.layout.layout_home_top_settings
        is HomeItems.Subject -> R.layout.row_subjects_home
        is HomeItems.Title -> com.atech.theme.R.layout.row_title
        HomeItems.DevNote -> com.atech.theme.R.layout.layout_note_from_dev
        is HomeItems.Holiday -> R.layout.row_holiday_home
        is HomeItems.Event -> R.layout.row_common_rv
        is HomeItems.Cgpa -> R.layout.row_cgpa_home
        is HomeItems.Attendance -> R.layout.row_attendance_rv
    }
}