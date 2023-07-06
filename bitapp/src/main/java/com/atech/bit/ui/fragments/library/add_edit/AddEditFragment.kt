package com.atech.bit.ui.fragments.library.add_edit

import android.Manifest
import android.animation.LayoutTransition
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentAddEditLibraryBookDetailsBinding
import com.atech.bit.utils.openKeyboard
import com.atech.core.utils.CalendarReminder
import com.atech.core.utils.DATE_PICKER_DIALOG
import com.atech.core.utils.DEFAULT_PAIR
import com.atech.core.utils.EDIT_TEXT_DATE_FORMAT
import com.atech.core.utils.convertLongToTime
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.compareDifferenceInDays
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.atech.theme.launchWhenStarted
import com.atech.theme.openAppSettings
import com.atech.theme.set
import com.atech.theme.showSnackBar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit_library_book_details) {

    private val binding: FragmentAddEditLibraryBookDetailsBinding by viewBinding()
    private val viewModel: AddEditViewModel by viewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    private fun checkNotificationPermission() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
//                    datePicker(binding.textFieldReminderDate.editText!!) { c ->
//                        addOrUpdateEventAndReminder(c)
//                    }
                } else {
                    binding.root.showSnackBar(
                        "Please grant Calendar permission from App Settings",
                        Snackbar.LENGTH_LONG,
                        actionName = "Settings",
                    ) {
                        requireContext().openAppSettings()
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        checkNotificationPermission()
        binding.apply {
            setView()
            setToolbar()
            textFieldIssueDate.openDatePicker {
                viewModel.libraryModel = viewModel.libraryModel.copy(issueDate = it.timeInMillis)
            }
            textFieldReturnDate.openDatePicker {
                viewModel.libraryModel = viewModel.libraryModel.copy(returnDate = it.timeInMillis)
            }

            textFieldReminderData()
            addOrRemoveReminder()
            removeEvent()
            saveOrCancel()
        }
        customBackPress {
            checkDataIsSaved()
        }
    }

    private fun setView() = binding.apply {
        textFieldBookName.editText?.setText(viewModel.libraryModel.bookName)
        textFieldBookName.requestFocus()
        requireContext().openKeyboard(textFieldBookName.editText!!)
        textFieldBookId.editText?.setText(viewModel.libraryModel.bookId)
        if (viewModel.libraryModel.issueDate != 0L) {
            textFieldIssueDate.editText?.setText(
                viewModel.libraryModel.issueDate.convertLongToTime(
                    EDIT_TEXT_DATE_FORMAT
                )
            )
        }
        if (viewModel.libraryModel.returnDate != 0L) {
            textFieldReturnDate.editText?.setText(
                viewModel.libraryModel.returnDate.convertLongToTime(
                    EDIT_TEXT_DATE_FORMAT
                )
            )
        }
        if (viewModel.libraryModel.alertDate != 0L && viewModel.libraryModel.eventId != -1L) {
            textFieldReminderDate.editText?.setText(
                viewModel.libraryModel.alertDate.convertLongToTime(
                    EDIT_TEXT_DATE_FORMAT
                )
            )
            textViewAddReminder.text = resources.getString(com.atech.theme.R.string.show_less)
            imageButtonRemoveEvent.visibility = View.VISIBLE
            textFieldReminderDate.visibility = View.VISIBLE
        }
    }

    private fun saveOrCancel() {
        binding.buttonSave.setOnClickListener {
            if (binding.textFieldBookName.checkIfEmpty())
                return@setOnClickListener
            val bookName = binding.textFieldBookName.editText?.text.toString()
            val bookId = binding.textFieldBookId.editText?.text.toString()
            if (binding.textFieldIssueDate.checkIfEmpty() || binding.textFieldReturnDate.checkIfEmpty())
                return@setOnClickListener

            val date = Date(viewModel.libraryModel.returnDate)
                .compareDifferenceInDays(Date(viewModel.libraryModel.issueDate))
            if (date < 0) {
                binding.textFieldReturnDate.error = "date is not valid"
                Toast.makeText(
                    requireContext(),
                    "Return date should be greater than issue date",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.libraryModel = viewModel.libraryModel.copy(
                bookName = bookName,
                bookId = bookId
            )
            if (viewModel.title == resources.getString(com.atech.theme.R.string.add_books))
                viewModel.addBook(viewModel.libraryModel)
            else
                viewModel.updateBook(viewModel.libraryModel)
            findNavController().navigateUp()
        }
        binding.buttonCancel.setOnClickListener {
            checkDataIsSaved()
        }
    }


    private fun FragmentAddEditLibraryBookDetailsBinding.addOrRemoveReminder() {
        textViewAddReminder.setOnClickListener {
            val isVisible = textFieldReminderDate.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(
                binding.root, TransitionSet().addTransition(Fade())
            )
            textViewAddReminder.text =
                if (isVisible) getString(com.atech.theme.R.string.add_reminder)
                else getString(com.atech.theme.R.string.show_less)

            root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            imageButtonRemoveEvent.visibility = if (isVisible) View.GONE else View.VISIBLE
            textFieldReminderDate.apply {
                visibility = if (isVisible) View.GONE else View.VISIBLE
                if (isVisible && viewModel.libraryModel.eventId == -1L) {
                    editText?.text?.clear()
                }
            }
        }
    }

    private fun removeEvent() {
        binding.imageButtonRemoveEvent.apply {
            setOnClickListener {
                if (viewModel.libraryModel.eventId != -1L)
                    showConfirmationDeleteReminderDialog {
                        binding.textFieldReminderDate.editText?.text?.clear()
                        binding.textFieldReminderDate.error = null
                    }
                else {
                    binding.textFieldReminderDate.editText?.text?.clear()
                    binding.textFieldReminderDate.error = null
                }
            }
        }
    }


    private fun TextInputLayout.openDatePicker(action: (Calendar) -> Unit = {}) {
        editText?.setOnClickListener {
            datePicker(editText!!) {
                action.invoke(it)
            }
        }
    }

    private fun datePicker(
        editText: EditText,
        validation: () -> Boolean = { false },
        action: (Calendar) -> Unit = {}
    ) {

        if (validation.invoke())
            return


        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build().apply {
                this.addOnPositiveButtonClickListener {
                    editText.setText(this.headerText)
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it
                    action(calendar)
                }
            }

        datePicker.show(childFragmentManager, DATE_PICKER_DIALOG)
    }


    private fun FragmentAddEditLibraryBookDetailsBinding.textFieldReminderData() {
        textFieldReminderDate.apply {
            editText?.setOnClickListener {
                if (checkWritePermission() == true && checkReadPermission() == true) {
                    datePicker(editText!!,
                        validation = {
                            validateTextFields()
                        }) {
                        addOrUpdateEventAndReminder(it)
                    }
                }
            }
        }
    }

    private fun FragmentAddEditLibraryBookDetailsBinding.validateTextFields() =
        (textFieldBookName.checkIfEmpty()
                || textFieldIssueDate.checkIfEmpty()
                || textFieldReturnDate.checkIfEmpty())

    private fun checkWritePermission(): Boolean? {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_CALENDAR,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR)
            null
        }
    }

    private fun checkReadPermission(): Boolean? {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CALENDAR,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
            null
        }
    }

    private fun addOrUpdateEventAndReminder(calendar: Calendar) =
        launchWhenStarted {
            val issueDayDifferance = Date(calendar.timeInMillis).compareDifferenceInDays(
                Date(viewModel.libraryModel.issueDate)
            )
            val returnDayDifferance = Date(calendar.timeInMillis).compareDifferenceInDays(
                Date(viewModel.libraryModel.returnDate)
            )
            if (issueDayDifferance < 0) {
                binding.textFieldReminderDate.error = "date is not valid"
                Toast.makeText(
                    requireContext(),
                    "Reminder date should be greater than issue date",
                    Toast.LENGTH_SHORT
                ).show()
                return@launchWhenStarted
            }
            if (returnDayDifferance > 0) {
                binding.textFieldReminderDate.error = "date is not valid"
                Toast.makeText(
                    requireContext(),
                    "Reminder date should be less than return date",
                    Toast.LENGTH_SHORT
                ).show()
                return@launchWhenStarted
            }

            if (viewModel.libraryModel.eventId == -1L) {
                addNewEventAndReminder(calendar)
            } else {
                updateEvent(calendar)
            }
        }

    private fun updateEvent(calendar: Calendar) {
        CalendarReminder.updateEventAndReminder(
            context = requireContext(),
            calendar = calendar,
            eventID = viewModel.libraryModel.eventId,
            setContent = {
                if (viewModel.libraryModel.bookName.isBlank())
                    DEFAULT_PAIR
                else
                    Pair(
                        viewModel.libraryModel.bookName,
                        "Return ${viewModel.libraryModel.bookName} to library"
                    )
            },
            action = {
                viewModel.libraryModel = viewModel.libraryModel.copy(
                    alertDate = calendar.timeInMillis
                )
                if (viewModel.libraryModel.bookName.isNotEmpty())
                    viewModel.updateBook(viewModel.libraryModel)
                showSnackBar("Reminder Updated")
            }
        )
    }


    private fun addNewEventAndReminder(calendar: Calendar) {
        CalendarReminder.addEventAndReminderToCalendar(
            context = requireContext(),
            calendar = calendar,
            setContent = {
                if (viewModel.libraryModel.bookName.isBlank())
                    DEFAULT_PAIR
                else
                    Pair(
                        viewModel.libraryModel.bookName,
                        "Return ${viewModel.libraryModel.bookName} to library"
                    )
            },
            error = {
                showSnackBar(
                    "Please grant Calendar permission from App Settings",
                )
            },
            action = { eventID ->
                if (eventID > 0) {
                    showSnackBar("Added successfully")
                    viewModel.libraryModel = viewModel.libraryModel.copy(
                        eventId = eventID,
                        alertDate = calendar.timeInMillis
                    )
                    if (viewModel.libraryModel.bookName.isNotEmpty())
                        viewModel.updateBook(viewModel.libraryModel)
                }
            }
        )
    }

    private fun checkDataIsSaved() {
        if (viewModel.libraryModel.bookName.isBlank() &&
            viewModel.title == resources.getString(com.atech.theme.R.string.add_books) &&
            viewModel.libraryModel.eventId != -1L
        ) {
            deleteEvent()
        }
        findNavController().navigateUp()

    }

    private fun deleteEvent() {
        CalendarReminder.deleteEvent(
            context = requireContext(),
            eventID = viewModel.libraryModel.eventId,
            action = {
                viewModel.libraryModel = viewModel.libraryModel.copy(
                    eventId = -1L,
                    alertDate = 0L
                )
                viewModel.updateBook(
                    viewModel.libraryModel
                )
                showSnackBar("Event deleted")
            },
            error = {
                showSnackBar(it)
            }
        )
    }

    private fun showConfirmationDeleteReminderDialog(action: () -> Unit = {}) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete reminder?")
            .setPositiveButton("Yes") { _, _ ->
                deleteEvent()
                action.invoke()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSnackBar(message: String) {
        binding.root.showSnackBar(message, Snackbar.LENGTH_SHORT)
    }

    private fun TextInputLayout.checkIfEmpty(): Boolean {
        return if (editText?.text?.isBlank() == true) {
            error = "Required"
            true
        } else {
            error = null
            false
        }
    }

    private fun FragmentAddEditLibraryBookDetailsBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                titleString = viewModel.title,
                action = findNavController()::navigateUp
            )
        )
    }

}