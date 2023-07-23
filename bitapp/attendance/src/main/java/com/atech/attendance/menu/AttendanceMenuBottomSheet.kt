package com.atech.attendance.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.atech.attendance.AttendanceViewModel
import com.atech.attendance.databinding.BottomSheetAttendanceMenuBinding
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.attendance.AttendanceSave
import com.atech.core.utils.REQUEST_MENU_FROM_ARCHIVE
import com.atech.core.utils.UPDATE_REQUEST
import com.atech.theme.base_class.BaseBottomSheet
import com.atech.theme.launchWhenStarted
import com.atech.theme.navigate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Deque

@AndroidEntryPoint
class AttendanceMenuBottomSheet : BaseBottomSheet() {
    private lateinit var binding: BottomSheetAttendanceMenuBinding

    private val args: AttendanceMenuBottomSheetArgs by navArgs()
    private val attendance: AttendanceModel by lazy {
        args.attendance
    }
    private val request by lazy {
        args.request
    }
    private val communicator: AttendanceViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAttendanceMenuBinding.inflate(inflater)
        binding.apply {
            ivArchive.apply {
                if (request == REQUEST_MENU_FROM_ARCHIVE) {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.atech.theme.R.drawable.ic_unarchive
                        )
                    )
                    binding.tvArchive.text = resources.getString(com.atech.theme.R.string.unarchive)
                } else {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            com.atech.theme.R.drawable.ic_archive
                        )
                    )
                    binding.tvArchive.text = resources.getString(com.atech.theme.R.string.archive)
                }
            }
            attendance.let { attendance ->
                bsUndo.setOnClickListener {
                    undoEntry(attendance)
                }
                bsEdit.setOnClickListener {
                    navigateToAddEditFragment(attendance)
                }
                bsDelete.setOnClickListener {
                    communicator.delete(attendance)
                    launchWhenStarted {
                        communicator._attendanceEvent.send(
                            AttendanceViewModel.AttendanceEvent.ShowUndoDeleteMessage(
                                attendance
                            )
                        )
                    }
                    dismiss()
                }
                binding.bsArchive.setOnClickListener {
                    //                    communicator.hasChange = true // FIXME: Work on Sync
                    if (request == REQUEST_MENU_FROM_ARCHIVE) {
                        communicator.update(attendance.copy(isArchive = false))
                        Toast.makeText(
                            requireContext(),
                            "Subject unarchived",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        communicator.update(attendance.copy(isArchive = true))
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
            communicator.update(att)
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
        val action =
            AttendanceMenuBottomSheetDirections.actionAttendanceMenuBottomSheetToAddEditAttendanceBottomSheet(
                attendance = attendance,
                type = UPDATE_REQUEST
            )
        navigate(action)
    }

}