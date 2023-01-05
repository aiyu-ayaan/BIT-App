package com.atech.bit.ui.fragments.attendance.add_from_online_syllabus

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.BottomSheetEditSubjectBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.utils.openBugLink
import com.atech.core.api.syllabus.SubjectModel
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.attendance.Days
import com.atech.core.utils.DataState
import com.atech.core.utils.REQUEST_EDIT_SUBJECT_FROM_SYLLABUS
import com.atech.core.utils.TAG
import com.atech.core.utils.UPDATE_REQUEST
import com.atech.core.utils.mergeList
import com.atech.core.utils.showSnackBar
import com.atech.core.utils.showUndoMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

@AndroidEntryPoint
class AddFromOnlineSyllabusBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetEditSubjectBinding
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private val viewModel: AddFromOnlineSyllabusViewModel by activityViewModels()
    private lateinit var onlineAdapter: OnlineSyllabusAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditSubjectBinding.inflate(layoutInflater)
        binding.bottomSheetTitle.text = resources.getString(R.string.add_subject_syllabus_online)
        getCurrentCourseSem()
        getAllOnlineSyllabusData()
        binding.ibDismiss.setOnClickListener {
            dismiss()
        }
        runBlocking { setRecyclerView() }
        return binding.root
    }

    private suspend fun setRecyclerView() = binding.showSyllabus.apply {
        adapter = OnlineSyllabusAdapter(
            viewModel.getAttendance(),
            viewLifecycleOwner,
            { onClick(it) }) { model, isChecked ->
            addOrRemoveData(model, isChecked)
        }.also { onlineAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun addOrRemoveData(model: SubjectModel, isChecked: Boolean) {
        if (isChecked)
            addSubjectToAttendanceDb(model)
        else
            removeSubjectFromAttendanceDb(model)
    }

    private fun removeSubjectFromAttendanceDb(model: SubjectModel) =
        lifecycleScope.launchWhenStarted {
            viewModel.findSyllabus(model.subjectName).let {
                it?.let {
                    viewModel.deleteAttendance(it)
                }
            }
        }

    private fun addSubjectToAttendanceDb(model: SubjectModel) {
        viewModel.addAttendance(
            AttendanceModel(
                subject = model.subjectName,
                present = 0,
                total = 0,
                fromSyllabus = true, days = Days(
                    presetDays = arrayListOf(),
                    absentDays = arrayListOf(),
                    totalDays = arrayListOf()
                ),
                teacher = "",
                fromOnlineSyllabus = true
            )
        ).also {
            binding.root.showSnackBar(
                "Added",
                Snackbar.LENGTH_SHORT
            )
        }
    }


    private fun getAllOnlineSyllabusData() = lifecycleScope.launchWhenStarted {
        viewModel.onlineSyllabus.collect { dataState ->
            when (dataState) {
                DataState.Empty -> {}
                is DataState.Error -> {
                    binding.progressLoadSubject.isVisible = false
                    binding.emptyAnimation.isVisible = true
                    if (dataState.exception is HttpException) {
                        binding.root.showSnackBar(
                            "${dataState.exception.message}", Snackbar.LENGTH_SHORT, "Report"
                        ) {
                            requireActivity().openBugLink(
                                com.atech.core.R.string.bug_repost,
                                "${this.javaClass.simpleName}.class",
                                dataState.exception.message
                            )
                        }
                    } else {
                        Log.d(TAG, "getOnlineSyllabus: ${dataState.exception.message}")
                    }
                }

                DataState.Loading -> {
                    binding.progressLoadSubject.isVisible = true
                }

                is DataState.Success -> {
                    binding.progressLoadSubject.isVisible = false
                    dataState.data.semester?.subjects?.let { subjects ->
                        mergeList(subjects.theory, subjects.lab, subjects.pe).also {
                            binding.emptyAnimation.isVisible = it.isEmpty()
                            onlineAdapter.submitList(it)
                        }
                    }
                    if (dataState.data.semester == null) {
                        binding.emptyAnimation.isVisible = true
                    }
                }
            }
        }
    }

    private fun getCurrentCourseSem() = lifecycleScope.launchWhenStarted {
        preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            viewModel.courseWithSem.value = it.courseWithSem.lowercase()
        }



        viewModel.editFromOnlineSyllabusFlow.asLiveData().observe(viewLifecycleOwner) {
            it.showUndoMessage(
                binding.root,
            ) { attendance ->
                viewModel.addAttendance(attendance.copy(fromSyllabus = true))
            }
        }
    }

    private fun onClick(it: SubjectModel) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val job = viewLifecycleOwner.lifecycleScope.async {
                val attendanceModel = viewModel.findSyllabus(it.subjectName)
                attendanceModel
            }
            val attendance = job.await()
            val action = NavGraphDirections.actionGlobalAddEditSubjectBottomSheet(
                attendance,
                UPDATE_REQUEST,
                REQUEST_EDIT_SUBJECT_FROM_SYLLABUS
            )
            findNavController().navigate(action)
        }
    }

}