package com.atech.attendance.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.attendance.AttendanceViewModel
import com.atech.attendance.adapter.AttendanceAdapter
import com.atech.attendance.adapter.AttendanceItem
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.utils.REQUEST_MENU_FROM_ARCHIVE
import com.atech.theme.base_class.BaseBottomSheet
import com.atech.theme.BottomSheetItem
import com.atech.theme.DialogModel
import com.atech.theme.R
import com.atech.theme.databinding.LayoutBottomSheetBinding
import com.atech.theme.navigate
import com.atech.theme.setTopView
import com.atech.theme.showDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchiveBottomSheet : BaseBottomSheet() {

    private val args: ArchiveBottomSheetArgs by navArgs()
    private val viewModel: AttendanceViewModel by activityViewModels()
    private val defPercentage: Int by lazy { args.defPercentage }
    private lateinit var binding: LayoutBottomSheetBinding
    private lateinit var attendanceAdapter: AttendanceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutBottomSheetBinding.inflate(inflater)
        binding.apply {
            setToolbar()
            setRecyclerview()
        }
        observeData()
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeData() {
        viewModel.archive.observe(viewLifecycleOwner) { attendanceList ->
            val data: MutableList<AttendanceItem> = attendanceList.map { attendanceModel ->
                AttendanceItem.AttendanceData(attendanceModel)
            } as MutableList<AttendanceItem>
            attendanceAdapter.items = data
            binding.ivEmpty.isVisible = data.isEmpty()
        }
    }

    private fun LayoutBottomSheetBinding.setRecyclerview() = this.listAll.apply {
        adapter = AttendanceAdapter(::onItemClick, ::onCheckClick, ::onWrongClick, ::onLongClick).also { attendanceAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onItemClick(model: AttendanceModel) {
        val action = ArchiveBottomSheetDirections.actionArchiveBottomSheetToDetailViewBottomSheet(
                model,
                model.subject,
                defPercentage
        )
        navigate(action)
    }

    private fun onCheckClick(model: AttendanceModel) {
        com.atech.attendance.utils.onCheckClick(viewModel, model)
    }

    private fun onWrongClick(model: AttendanceModel) {
        com.atech.attendance.utils.onWrongClick(viewModel, model)
    }

    private fun onLongClick(model: AttendanceModel) {
        val action = ArchiveBottomSheetDirections.actionArchiveBottomSheetToAttendanceMenuBottomSheet(
                model,
                REQUEST_MENU_FROM_ARCHIVE
        )
        navigate(action)
    }


    private fun LayoutBottomSheetBinding.setToolbar() = this.apply {
        setTopView(BottomSheetItem(getString(R.string.archive), R.drawable.ic_delete_all, onIconClick = {
            deleteAllArchive()// FIXME: communicator.hasChange = true
        }, textViewApply = {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_downward, 0, 0, 0)
            setOnClickListener {
                dismiss()
            }
        }))
    }

    private fun deleteAllArchive() = showDialog(
            DialogModel(
                    title = "Delete All Archive",
                    message = "Are you sure you want to delete all archive?",
                    positiveText = "Yes",
                    negativeText = "No",
                    positiveAction = {
                        viewModel.deleteAllArchive()
                        dismiss()
                    },
            )
    )
}