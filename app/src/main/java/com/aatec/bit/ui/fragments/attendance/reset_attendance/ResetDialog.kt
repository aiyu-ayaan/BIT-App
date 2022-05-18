package com.aatec.bit.ui.fragments.attendance.reset_attendance

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.aatec.core.data.room.attendance.AttendanceSave
import com.aatec.core.data.room.attendance.Days
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ResetDialog : DialogFragment() {
    private val viewModel: ResetViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Do you want to reset ${viewModel.attendance!!.subject}?")
            .setPositiveButton("Yes") { _, _ ->
                val attendance = viewModel.attendance
                if (attendance != null) {
                    val ques: Deque<AttendanceSave> = ArrayDeque()
                    viewModel.update(
                        attendance.copy(
                            present = 0,
                            total = 0,
                            days = Days(
                                presetDays = ArrayList(),
                                absentDays = ArrayList(),
                                totalDays = ArrayList()
                            ),
                            stack = ques,
                            teacher = "",
                            created = System.currentTimeMillis()
                        )
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "We ran at some problem !!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.setNegativeButton("No", null)
            .create()
}