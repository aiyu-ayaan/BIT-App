package com.aatec.bit.ui.fragments.list_all

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.ui.custom_views.DividerItemDecorationNoLast
import com.aatec.bit.databinding.BottomSheetListAllBinding
import com.aatec.bit.utils.AttendanceEvent
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import com.aatec.core.utils.REQUEST_EDIT_SUBJECT_FROM_LIST_ALL
import com.aatec.core.utils.UPDATE_REQUEST
import com.aatec.core.utils.showUndoMessage
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
                    )
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
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
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