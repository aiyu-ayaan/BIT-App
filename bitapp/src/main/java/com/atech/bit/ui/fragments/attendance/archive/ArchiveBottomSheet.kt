package com.atech.bit.ui.fragments.attendance.archive

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.BottomSheetArchiveBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.bit.ui.fragments.attendance.AttendanceAdapter
import com.atech.bit.utils.launchWhenStarted
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.attendance.AttendanceSave
import com.atech.core.data.room.attendance.IsPresent
import com.atech.core.utils.REQUEST_MENU_FROM_ARCHIVE
import com.atech.core.utils.convertLongToTime
import com.atech.core.utils.countTotalClass
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Deque

@AndroidEntryPoint
class ArchiveBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog

    private lateinit var binding: BottomSheetArchiveBinding
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val viewModel: ArchiveViewModel by viewModels()
    private val communicator: CommunicatorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetArchiveBinding.inflate(inflater)
        attendanceAdapter = AttendanceAdapter(
            {
                onItemClickListener(it)
            },
            { onCheckClick(it) },
            { onWrongClick(it) },
            {
                navigateToMenu(it)
            }
        )
        binding.apply {
            bottomSheetTitle.setOnClickListener {
                dismiss()
            }
            listAll.apply {
                adapter = attendanceAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            btDeleteAll.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete All")
                    .setMessage("Are you sure you want to delete all archive attendance?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.deleteAll()
                        communicator.hasChange = true
                        dismiss()
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .show()
            }
        }
        getData()
        return binding.root
    }

    private fun getData() = launchWhenStarted {
        viewModel.archiveAttendance.collect {
            binding.ivEmpty.isVisible = it.isEmpty()
            attendanceAdapter.submitList(it)
        }
    }

    private fun onItemClickListener(attendance: AttendanceModel) {
        try {
            val action =
                ArchiveBottomSheetDirections.actionArchiveBottomSheetToCalenderViewBottomSheet(
                    attendance,
                    attendance.subject,
                    viewModel.defPercentage
                )
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Click one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onCheckClick(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val presentDays = attendance.days.presetDays.clone() as ArrayList<Long>
        val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>
        stack.push(
            AttendanceSave(
                attendance.total,
                attendance.present,
                attendance.days.copy(
                    presetDays = attendance.days.presetDays,
                    totalDays = ArrayList(attendance.days.totalDays.map {
                        IsPresent(
                            it.day,
                            it.isPresent,
                            it.totalClasses
                        )
                    })
                )
            )
        )
        presentDays.add(System.currentTimeMillis())
        /**
         * @since 4.0.3
         * @author Ayaan
         */
        when {
            totalDays.isEmpty() ||
                    totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") || !totalDays.last().isPresent ->//new Entry or new day or new session
                totalDays.add(IsPresent(System.currentTimeMillis(), true, totalClasses = 1))

            totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
                totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, true)

            else ->//same day
                totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
        }
        viewModel.update(
            AttendanceModel(
                id = attendance.id,
                subject = attendance.subject,
                present = attendance.present + 1,
                total = attendance.total + 1,
                days = attendance.days.copy(presetDays = presentDays, totalDays = totalDays),
                stack = stack,
                fromSyllabus = attendance.fromSyllabus,
                teacher = attendance.teacher,
                isArchive = attendance.isArchive
            )
        ).also {
            communicator.hasChange = true
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onWrongClick(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val absentDays = attendance.days.absentDays.clone() as ArrayList<Long>
        val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>
        stack.push(
            AttendanceSave(
                attendance.total,
                attendance.present,
                attendance.days.copy(
                    absentDays = attendance.days.absentDays,
                    totalDays = ArrayList(attendance.days.totalDays.map {
                        IsPresent(
                            it.day,
                            it.isPresent,
                            it.totalClasses
                        )
                    })
                ),
            )
        )
        absentDays.add(System.currentTimeMillis())
        /**
         * @since 4.0.3
         * @author Ayaan
         */
        when {
            totalDays.isEmpty() || totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") || totalDays.last().isPresent ->//new Entry or new day
                totalDays.add(IsPresent(System.currentTimeMillis(), false, totalClasses = 1))

            totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
                totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, false)

            else ->//same day
                totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
        }
        viewModel.update(
            AttendanceModel(
                id = attendance.id,
                subject = attendance.subject,
                present = attendance.present,
                total = attendance.total + 1,
                days = attendance.days.copy(
                    absentDays = absentDays,
                    totalDays = totalDays
                ),
                stack = stack,
                fromSyllabus = attendance.fromSyllabus,
                teacher = attendance.teacher,
                isArchive = attendance.isArchive
            ).also {
                communicator.hasChange = true
            }
        )

    }

    private fun navigateToMenu(attendanceModel: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalAttendanceMenu(
            attendanceModel,
            REQUEST_MENU_FROM_ARCHIVE
        )
        findNavController().navigate(action)
    }

}