package com.atech.bit.ui.fragments.attendance.menu

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.BottomSheetAttendanceBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.core.utils.AttendanceEvent
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.attendance.AttendanceSave
import com.atech.core.utils.REQUEST_MENU_FROM_ARCHIVE
import com.atech.core.utils.UPDATE_REQUEST
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Deque

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
            ivArchive.apply {
                if (viewModel.request == REQUEST_MENU_FROM_ARCHIVE) {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_unarchive
                        )
                    )
                    binding.tvArchive.text = resources.getString(R.string.unarchive)
                } else {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_archive
                        )
                    )
                    binding.tvArchive.text = resources.getString(R.string.archive)
                }
            }
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
                binding.bsArchive.setOnClickListener {
                    communicator.hasChange = true
                    if (viewModel.request == REQUEST_MENU_FROM_ARCHIVE) {
                        viewModel.update(attendance.copy(isArchive = false))
                        Toast.makeText(
                            requireContext(),
                            "Subject unarchived",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.update(attendance.copy(isArchive = true))
                        Toast.makeText(
                            requireContext(),
                            "Subject archived",
                            Toast.LENGTH_SHORT
                        ).show()
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