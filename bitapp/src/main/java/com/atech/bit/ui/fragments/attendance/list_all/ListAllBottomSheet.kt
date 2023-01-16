package com.atech.bit.ui.fragments.attendance.list_all

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.BottomSheetListAllBinding
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.utils.launchWhenStarted
import com.atech.core.utils.AttendanceEvent
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import com.atech.core.utils.REQUEST_EDIT_SUBJECT_FROM_LIST_ALL
import com.atech.core.utils.UPDATE_REQUEST
import com.atech.core.utils.showUndoMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListAllBottomSheet : BottomSheetDialogFragment(), ListAllAdapter.ClickListenerListAll {

    private lateinit var binding: BottomSheetListAllBinding
    private val viewModel: ListAllViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetListAllBinding.inflate(inflater)

        val listAllAdapter = ListAllAdapter(this)
        binding.apply {
            listAll.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                    }
                )
                adapter = listAllAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            btDeleteAll.setOnClickListener {
                val action = NavGraphDirections.actionGlobalDeleteAllDialog()
                findNavController().navigate(action)
            }
            bottomSheetTitle.setOnClickListener {
                dismiss()
            }
            ivAdd.setOnClickListener {
                val action = NavGraphDirections.actionGlobalAddEditSubjectBottomSheet()
                findNavController().navigate(action)
            }
        }
        viewModel.allAttendance.observe(viewLifecycleOwner) {
            binding.ivAdd.isVisible = it.isEmpty()
            listAllAdapter.submitList(it)
        }
        launchWhenStarted {
            viewModel.attendanceEvent.collect { event ->
                when (event) {
                    is AttendanceEvent.ShowUndoDeleteMessage -> event.attendance.showUndoMessage(
                        binding.root
                    ) {
                        viewModel.add(it, REQUEST_ADD_SUBJECT_FROM_SYLLABUS)
                    }
                }
            }
        }
        return binding.root
    }

    override fun setEditClick(attendance: AttendanceModel) {
        navigateToAddEditFragment(attendance)
    }


    /**
     * @version 4.0.4
     * @author Ayaan
     */
    private fun navigateToAddEditFragment(attendance: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalAddEditSubjectBottomSheet(
            attendance,
            UPDATE_REQUEST,
            request = REQUEST_EDIT_SUBJECT_FROM_LIST_ALL
        )
        findNavController().navigate(action)
    }

    override fun setResetClick(attendance: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalResetDialog(attendance)
        findNavController().navigate(action)
    }

    override fun setDeleteClick(attendance: AttendanceModel) {
        viewModel.delete(attendance)
    }
}