package com.atech.attendance.add_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atech.attendance.databinding.BottomSheetAddEditBinding
import com.atech.core.room.attendance.AttendanceSave
import com.atech.core.utils.ERROR_IN_UPDATE
import com.atech.core.utils.REQUEST_EDIT_SUBJECT_FROM_LIST_ALL
import com.atech.theme.base_class.BaseBottomSheet
import com.atech.theme.getAndSetHint
import com.atech.theme.launchWhenStarted
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayDeque
import java.util.Deque

@AndroidEntryPoint
class AddEditAttendanceBottomSheet : BaseBottomSheet() {
    private lateinit var binding: BottomSheetAddEditBinding
    private val viewModel: AddEditViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddEditBinding.inflate(inflater)
        setViews()
        return binding.root
    }

    /**
     * Set all the views
     */
    private fun setViews() {
        binding.apply {
            binding.bottomSheetTitle.text = viewModel.type
//          Populating editTextViews
            outlinedTextFieldSubject.hint = viewModel.subjectName
            when (viewModel.request) {
                0 -> outlinedTextFieldSubject.requestFocus()
                REQUEST_EDIT_SUBJECT_FROM_LIST_ALL -> {
                    outlinedTextFieldSubject.requestFocus()
                }

                else -> {
                    outlinedTextFieldSubject.endIconMode = TextInputLayout.END_ICON_NONE
                    outlinedTextFieldTeacher.requestFocus()
                    outlinedTextFieldSubject.isEnabled = false
                }
            }

            setEndButtonClickListener()

            if (viewModel.type != "Update") {
                outlinedTextFieldPresent.endIconMode = TextInputLayout.END_ICON_NONE
                outlinedTextFieldTotal.endIconMode = TextInputLayout.END_ICON_NONE
                outlinedTextFieldTeacher.endIconMode = TextInputLayout.END_ICON_NONE
                outlinedTextFieldSubject.endIconMode = TextInputLayout.END_ICON_NONE
            }

            outlinedTextFieldPresent.hint = setHintPresent()
            outlinedTextFieldTotal.hint = setHintTotal()
            outlinedTextFieldTeacher.hint = setHintTeacher()

            saveSubject.contentDescription = viewModel.type
            saveSubject.setOnClickListener {
                val subject = when (viewModel.type) {
                    "Update" -> when {
                        editTextSubject.text.toString().isBlank() ->
                            editTextSubject.hint.toString()

                        else -> editTextSubject.text.toString()
                    }

                    else -> editTextSubject.text.toString()
                }
                if (subject.isBlank()) {
                    outlinedTextFieldSubject.error = "Can't be empty"
                    return@setOnClickListener
                }
                val present = when {
                    editTextPresent.text!!.isBlank() -> {
                        editTextPresent.hint.toString()
                                .getAndSetHint(resources.getString(com.atech.theme.R.string.initial_present))
                    }

                    else -> editTextPresent.text.toString().toInt()
                }
                val total = when {
                    editTextTotal.text!!.isBlank() -> {
                        editTextTotal.hint.toString()
                                .getAndSetHint(resources.getString(com.atech.theme.R.string.initial_total))
                    }

                    else -> editTextTotal.text.toString().toInt()
                }
                val teacher = when (viewModel.type) {
                    "Update" -> when {
                        editTextTeacher.text.toString().isBlank() ->
                            if (editTextTeacher.hint.toString() == resources.getString(com.atech.theme.R.string.teacher_name)
                                    || editTextTeacher.hint.toString() == resources.getString(com.atech.theme.R.string.no_teacher_name)
                            ) "" else editTextTeacher.hint.toString()

                        else -> if (editTextTeacher.text.toString() == resources.getString(com.atech.theme.R.string.teacher_name)
                                || editTextTeacher.text.toString() == resources.getString(com.atech.theme.R.string.no_teacher_name)
                        ) "" else editTextTeacher.text.toString()
                    }

                    else -> editTextTeacher.text.toString()
                }
                validateEntry(subject, teacher, present, total)
            }
            bottomSheetTitle.setOnClickListener {
                dismiss()
            }
        }
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun setEndButtonClickListener() {
        binding.apply {
            outlinedTextFieldSubject.setEndIconOnClickListener {
                editTextSubject.setText(editTextSubject.hint.toString())
            }

            outlinedTextFieldTotal.setEndIconOnClickListener {
                editTextTotal.setText(editTextTotal.hint.toString())
            }

            outlinedTextFieldPresent.setEndIconOnClickListener {
                editTextPresent.setText(editTextPresent.hint.toString())
            }

            outlinedTextFieldTeacher.setEndIconOnClickListener {
                editTextTeacher.setText(editTextTeacher.hint.toString())
            }
            outlinedTextFieldPresent.setErrorIconOnClickListener {
                val present = binding.editTextPresent.text
                val total = binding.editTextTotal.text
                binding.editTextPresent.text = total
                binding.editTextTotal.text = present
            }

        }
    }


    /**
     * @since 4.0.3
     * @author Ayaan
     */
    private fun setHintPresent(): String =
            when {
                viewModel.attendance.subject == "Subject" &&
                        viewModel.attendance.present == 0
                -> resources.getString(com.atech.theme.R.string.initial_present)

                else -> "${viewModel.attendance.present}"
            }

    /**
     * @since 4.0.3
     * @author Ayaan
     */
    private fun setHintTotal(): String =
            when {
                viewModel.attendance.subject == "Subject" &&
                        viewModel.attendance.total == 0
                -> resources.getString(com.atech.theme.R.string.initial_total)

                else -> "${viewModel.attendance.total}"
            }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun setHintTeacher(): String =
            when {
                viewModel.attendance.subject == "Subject" &&
                        viewModel.attendance.teacher == ""
                -> resources.getString(com.atech.theme.R.string.teacher_name)

                else -> if (viewModel.attendance.teacher == "") resources.getString(com.atech.theme.R.string.no_teacher_name)
                else "${viewModel.attendance.teacher}"
            }


    private fun validateEntry(subject: String, teacher: String, present: Int, total: Int) {
        when {
            present > total -> {
                binding.outlinedTextFieldPresent.error =
                        "Check your input ($present > $total).\nPress error icon to flip value !!"
            }

            else -> addOrUpdateData(subject, teacher, present, total)
        }
    }


    private fun addOrUpdateData(subject: String, teacher: String, present: Int, total: Int) =
            launchWhenStarted {
                when (viewModel.type) {
                    "Update" ->
                        when (updateLogic(subject, teacher, present, total)) {
                            ERROR_IN_UPDATE -> {
                                Toast.makeText(
                                        requireContext(),
                                        com.atech.theme.R.string.uniques_key_warning,
                                        Toast.LENGTH_SHORT
                                ).show()
                                return@launchWhenStarted
                            }
                        }

                    else -> {
                        viewModel.add(
                                viewModel.attendance.copy(
                                        subject = subject,
                                        present = present,
                                        total = total,
                                        teacher = teacher
                                )
                        )

                    }
                }
                Toast.makeText(requireContext(), "Done \uD83D\uDE07 !!", Toast.LENGTH_SHORT).show()
                clearFocus()
                findNavController().popBackStack()
            }

    private fun clearFocus() = binding.apply {
        outlinedTextFieldSubject.clearFocus()
        outlinedTextFieldTotal.clearFocus()
        outlinedTextFieldPresent.clearFocus()
    }

    private suspend fun updateLogic(
            subjectName: String,
            teacher: String,
            present: Int,
            total: Int
    ) =
            withContext(Dispatchers.IO) {
                when {
                    subjectName != viewModel.attendance.subject &&
                            teacher != viewModel.attendance.teacher &&
                            present == viewModel.subjectPresent &&
                            total == viewModel.subjectTotal || subjectName == viewModel.attendance.subject &&
                            present == viewModel.subjectPresent &&
                            total == viewModel.subjectTotal -> {

                        viewModel.update(
                                viewModel.attendance.copy(
                                        subject = subjectName,
                                        present = present,
                                        total = total,
                                        teacher = teacher
                                )
                        )
                    }

                    else -> {
                        val ques: Deque<AttendanceSave> = ArrayDeque()
                        viewModel.update(
                                viewModel.attendance.copy(
                                        subject = subjectName,
                                        present = present,
                                        total = total,
                                        stack = ques,
                                        teacher = teacher
                                )
                        )
                    }
                }
            }


}