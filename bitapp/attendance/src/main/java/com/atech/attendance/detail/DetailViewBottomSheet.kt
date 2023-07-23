package com.atech.attendance.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.attendance.databinding.BottomSheetDetailViewBinding
import com.atech.attendance.detail.adapter.HistoryAdapter
import com.atech.attendance.utils.calculatedDays
import com.atech.attendance.utils.findPercentage
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.attendance.IsPresent
import com.atech.core.utils.convertLongToTime
import com.atech.theme.base_class.BaseBottomSheet
import com.atech.theme.R
import com.atech.theme.launchWhenStarted
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil


@AndroidEntryPoint
class DetailViewBottomSheet : BaseBottomSheet() {


    private val args: DetailViewBottomSheetArgs by navArgs()

    private val title: String by lazy {
        args.title
    }
    private val attendance: AttendanceModel by lazy {
        args.attendance
    }

    private val minPercentage: Int by lazy {
        args.minPercentage
    }

    private lateinit var binding: BottomSheetDetailViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDetailViewBinding.inflate(inflater)
        setViews()
        val historyAdapter = HistoryAdapter()
        val list = mutableListOf<IsPresent>()
        binding.apply {
            bottomSheetTitle.text = title
            tvMonth.text = SimpleDateFormat(
                "MMMM yyyy",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            calendarView.shouldDrawIndicatorsBelowSelectedDays(true)
            calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
                override fun onDayClick(dateClicked: Date?) {}
                override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                    tvMonth.text = SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.getDefault()
                    ).format(firstDayOfNewMonth!!)
                    setDates(firstDayOfNewMonth, historyAdapter, list)
                }
            })
            setDates(Date(), historyAdapter, list)
            bottomSheetTitle.setOnClickListener {
                dismiss()
            }
            btDismiss.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }

    private fun setViews() {
        val attendance = attendance
        attendance.let {
            setUpPercentage(it)
            setUpCreated(it)
        }
        attendance.let {
            binding.apply {
                launchWhenStarted {
                    withContext(Dispatchers.IO) {
                        val event = mutableListOf<Event>()
                        attendance.days.totalDays.asReversed().forEach {
                            it.totalClasses?.let { totalClasses ->
                                var v = totalClasses
                                while (v-- != 0) {
                                    event.add(
                                        Event(
                                            getColor(it.isPresent)!!,
                                            it.day
                                        )
                                    )
                                }
                            }
                        }
                        binding.calendarView.addEvents(event)
                    }
                }
            }
        }
    }

    private fun setUpCreated(it: AttendanceModel) {
        binding.tvTeacherName.text =
            if (it.teacher.equals("")) resources.getString(R.string.no_teacher_name) else it.teacher
        binding.tvCreated.text =
            resources.getString(
                R.string.created,
                if (it.created == null) resources.getString(R.string.na) else it.created!!.convertLongToTime(
                    resources.getString(R.string.default_time_formate_without_time)
                )
            )
    }

    private fun getColor(isPresent: Boolean): Int? =
        when {
            isPresent -> context?.getColor(R.color.green)
            else -> context?.getColor(R.color.red)
        }

    private fun setDates(date: Date, historyAdapter: HistoryAdapter, list: MutableList<IsPresent>) {
        val attendance = attendance
        list.clear()
        val sf = SimpleDateFormat("MMMM/yyyy", Locale.getDefault())
        val compare = sf.format(date)
        attendance.days.totalDays.getOnly50Data().forEach {
            if (compare.equals(sf.format(it.day))) {
                list.add(it)
            }
        }
        binding.cardViewHistory.isVisible = attendance.days.totalDays.isNotEmpty()
        binding.showDates.showHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        historyAdapter.submitList(list)
    }


    private fun ArrayList<IsPresent>.getOnly50Data(): ArrayList<IsPresent> {
        val list = arrayListOf<IsPresent>()
        this.asReversed().forEach { isPresent ->
            if (list.size < 50) {
                list.add(isPresent)
            }
        }
        return list
    }

    private fun setUpPercentage(model: AttendanceModel) {
        val percentage = findPercentage(
            model.present.toFloat(),
            model.total.toFloat()
        ) { present, total ->
            when (total) {
                0.0F -> 0.0F
                else -> (present / total) * 100
            }
        }

        when {
            percentage.toInt() == 0 -> {
                binding.tvStatus.text = resources.getString(R.string.blank)
            }

            percentage >= minPercentage -> {
                binding.progressBarOuter.setIndicatorColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.green
                    )
                )
                val day = calculatedDays(model.present, model.total) { present, total ->
                    (((100 * present) - (minPercentage * total)) / minPercentage)
                }.toInt()
                binding.tvStatus.text = when {
                    percentage.toInt() == minPercentage || day <= 0 ->
                        resources.getString(R.string.on_track)

                    day != 0 -> resources.getString(R.string.leave_class, day.toString())
                    else -> resources.getString(R.string.error)
                }
            }

            percentage < minPercentage -> {
                binding.progressBarOuter.setIndicatorColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red
                    )
                )
                val day = calculatedDays(model.present, model.total) { present, total ->
                    (((minPercentage * total) - (100 * present)) / (100 - minPercentage))
                }
                binding.tvStatus.text =
                    resources.getString(R.string.attend_class, (ceil(day).toInt()).toString())
            }
        }
        binding.tvPercentage.text = resources.getString(
            R.string.present_total,
            model.present.toString(),
            model.total.toString()
        )
        binding.progressBarOuter.progress = percentage.toInt()
    }
}