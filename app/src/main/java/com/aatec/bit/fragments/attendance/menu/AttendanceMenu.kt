package com.aatec.bit.fragments.attendance.menu

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.CommunicatorViewModel
import com.aatec.bit.databinding.BottomSheetAddSubjectBinding
import com.aatec.bit.databinding.BottomSheetAttendanceBinding
import com.aatec.bit.utils.AttendanceEvent
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.attendance.AttendanceSave
import com.aatec.core.utils.UPDATE_REQUEST
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AttendanceMenu : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAttendanceBinding

    private val viewModel: MenuViewModel by viewModels()
    private val communicator: CommunicatorViewModel by activityViewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAttendanceBinding.inflate(inflater)
        binding.apply {
            viewModel.attendance?.let { attendance ->
                bsUndo.setOnClickListener {
                    undoEntry(attendance)
                }
                bsEdit.setOnClickListener {
                    navigateToAddEditFragment(attendance)
                }
                bsDelete.setOnClickListener {
                    viewModel.delete(attendance)
                    lifecycleScope.launchWhenStarted {
                        communicator._attendanceEvent.send(
                            AttendanceEvent.ShowUndoDeleteMessage(
                                attendance
                            )
                        )
                    }
                    dismiss()
                }
            }
        }
        binding.ibDismiss.setOnClickListener {
            dismiss()
        }
//        showUndoMessage()
        return binding.root
    }


    private fun undoEntry(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val save = stack.peekFirst()
        if (save != null) {
            stack.pop()
            val att = attendance.copy(
                present = save.present,
                total = save.total,
                days = save.days,
                stack = stack,
            )
            viewModel.update(att)
            dismiss()
        } else {
            Toast.makeText(context, "Stack is empty !!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @version 4.0.3
     * @author Ayaan
     */
    private fun navigateToAddEditFragment(attendance: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalAddEditSubjectBottomSheet(
            attendance,
            UPDATE_REQUEST
        )
        findNavController().navigate(action)
    }

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog
}