package com.atech.attendance.add_from_syllabus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.core.utils.REQUEST_EDIT_SUBJECT_FROM_SYLLABUS
import com.atech.core.utils.UPDATE_REQUEST
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.utils.RowSubjectAdapter
import com.atech.course.utils.tabSelectedListener
import com.atech.theme.base_class.BaseBottomSheet
import com.atech.theme.BottomSheetItem
import com.atech.theme.R
import com.atech.theme.databinding.LayoutBottomSheetBinding
import com.atech.theme.launch
import com.atech.theme.launchWhenCreated
import com.atech.theme.navigate
import com.atech.theme.setTopView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddFromSyllabusBottomSheet : BaseBottomSheet() {
    private lateinit var binding: LayoutBottomSheetBinding

    private val viewModel: AddFromSyllabusViewModel by viewModels()
    private lateinit var rowAdapter: RowSubjectAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetBinding.inflate(inflater)
        binding.apply {
            setToolbar()
            setRecyclerView()
            setTabView()
        }
        observeData()
        return binding.root
    }

    private fun LayoutBottomSheetBinding.setRecyclerView() = this.listAll.apply {
        adapter = RowSubjectAdapter(
            onEditClick = ::onEditClick, onItemClick = ::onItemClick
        ).also { rowAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onItemClick(model: SyllabusUIModel, isChecked: Boolean) {
        if (isChecked)
            viewModel.addSubject(model.subject)
        else
            viewModel.removeSubject(model.subject)
    }

    private fun onEditClick(model: SyllabusUIModel) = launch {
        val item = viewModel.findAttendance(model)
        val action =
            AddFromSyllabusBottomSheetDirections.actionAddFromSyllabusBottomSheetToAddEditAttendanceBottomSheet(
                attendance = item,
                type = UPDATE_REQUEST,
                request = REQUEST_EDIT_SUBJECT_FROM_SYLLABUS
            )
        navigate(action)
    }

    private fun observeData() = launchWhenCreated {
        viewModel.getSubjects().collectLatest {
            rowAdapter.submitList(it)
        }
    }

    private fun LayoutBottomSheetBinding.setTabView() = this.tabLayoutType.apply {
        tabSelectedListener { tab ->
            viewModel.isOnline.value = tab?.text == getString(R.string.online)
        }
    }

    private fun LayoutBottomSheetBinding.setToolbar() = this.apply {
        tabLayoutType.isVisible = true
        setTopView(
            BottomSheetItem(
                getString(R.string.add_subject_syllabus),
                R.drawable.ic_arrow_downward,
                onIconClick = {
                    dismiss()
                })
        )
    }

}