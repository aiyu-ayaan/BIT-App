package com.aatec.bit.fragments.edit_subject

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.BottomSheetEditSubjectBinding
import com.aatec.bit.fragments.home.adapter.SyllabusHomeAdapter
import com.aatec.bit.utils.AttendanceEvent
import com.aatec.core.utils.showSnackBar
import com.aatec.core.utils.showUndoMessage
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.attendance.Days
import com.aatec.core.data.room.syllabus.SyllabusModel
import com.aatec.core.utils.REQUEST_ADAPTER_EDIT
import com.aatec.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import com.aatec.core.utils.REQUEST_EDIT_SUBJECT_FROM_SYLLABUS
import com.aatec.core.utils.UPDATE_REQUEST
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async

@AndroidEntryPoint
class EditSubjectBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetEditSubjectBinding
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private val viewModel: EditSyllabusViewModel by viewModels()
    private val args: EditSubjectBottomSheetArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditSubjectBinding.inflate(layoutInflater)

        val request = when (args.request) {
            REQUEST_ADD_SUBJECT_FROM_SYLLABUS -> {
                binding.bottomSheetTitle.text = resources.getString(R.string.add_subject_syllabus)
                REQUEST_ADD_SUBJECT_FROM_SYLLABUS
            }
            else -> REQUEST_ADAPTER_EDIT
        }
        val syllabusAdapter =
            SyllabusHomeAdapter(request = request, clickListener = { checkBox, syllabus ->
                checkBox.isChecked = !checkBox.isChecked
                when (args.request) {
                    REQUEST_ADD_SUBJECT_FROM_SYLLABUS -> addOrDeleteSubject(
                        checkBox.isChecked,
                        syllabus
                    )
                    else -> viewModel.updateSyllabus(syllabus.copy(isChecked = checkBox.isChecked))
                }
            }, editListener = {
                onClick(it)
            })
        binding.apply {
            showSyllabus.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = syllabusAdapter
                setHasFixedSize(true)
            }
        }
        viewModel.syllabus.observe(viewLifecycleOwner) {
            syllabusAdapter.submitList(it)
        }

        preferenceManager()
        setHasOptionsMenu(true)
        fragmentEditSyllabusEvent()
        binding.ibDismiss.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun onClick(it: SyllabusModel) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val job = viewLifecycleOwner.lifecycleScope.async {
                val attendanceModel = viewModel.getAttendance(it.subject)
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

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun fragmentEditSyllabusEvent() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.editSyllabusEventFlow.collect { attendanceEvent ->
                when (attendanceEvent) {
                    is AttendanceEvent.ShowUndoDeleteMessage ->
                        attendanceEvent.attendance.showUndoMessage(
                            binding.root,
                        ) { attendance ->
                            viewModel.add(attendance)
                            viewModel.updateSyllabus(attendanceEvent.syllabus!!.copy(isAdded = true))
                        }
                }
            }
        }
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun addOrDeleteSubject(checked: Boolean, syllabus: SyllabusModel) {

        when {
            checked -> {
                viewModel.add(
                    AttendanceModel(
                        subject = syllabus.subject,
                        present = 0,
                        total = 0,
                        fromSyllabus = true, days = Days(
                            presetDays = arrayListOf(),
                            absentDays = arrayListOf(),
                            totalDays = arrayListOf()
                        ),
                        teacher = ""
                    )
                )
                binding.root.showSnackBar(
                    "Added",
                    Snackbar.LENGTH_SHORT
                )
                viewModel.updateSyllabus(syllabus.copy(isAdded = true))
            }
            !checked
            -> {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    viewModel.updateSyllabus(syllabus.copy(isAdded = false))
                    viewModel.findSyllabus(syllabus.subject)?.let {
                        viewModel.delete(it, syllabus)
                    }
                }
            }
        }
    }

    private fun preferenceManager() {
        preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            viewModel.syllabusQuery.value = "${it.course}${it.sem}"
        }
    }
}