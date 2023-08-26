package com.atech.attendance.sort

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.atech.attendance.databinding.MenuSortBinding
import com.atech.core.datastore.DataStoreCases
import com.atech.core.room.attendance.Sort
import com.atech.core.room.attendance.SortBy
import com.atech.core.room.attendance.SortOrder
import com.atech.core.utils.capitalizeWords
import com.atech.theme.R
import com.atech.theme.launchWhenStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SortMenuDialog : DialogFragment() {

    private lateinit var binding: MenuSortBinding

    @Inject
    lateinit var dataStoreCases: DataStoreCases

    private val args: SortMenuDialogArgs by navArgs()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = MenuSortBinding.inflate(layoutInflater)
        binding.apply {
            setView()
            setDropDown()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.sort))
            .setPositiveButton("Done") { _, _ ->
                saveData()
            }.setNegativeButton("Cancel", null)
            .create()
    }

    private fun saveData() = launchWhenStarted {
        val sortBy = SortBy.valueOf(
            (binding.textInputLayoutSortBy.editText as? MaterialAutoCompleteTextView)?.text.toString()
                .uppercase()
        )
        val sortOrder = SortOrder.valueOf(
            (binding.textInputLayoutSortOrder.editText as? MaterialAutoCompleteTextView)?.text.toString()
                .uppercase()
        )
        val sort = Sort(sortBy, sortOrder)
        Log.d("AAA", "saveData: $sort")
        dataStoreCases.updateAttendanceSort(sort)
    }

    private fun MenuSortBinding.setDropDown() = this.root.apply {
        val sortBy = SortBy.values().map { it.name.capitalizeWords() }.toTypedArray()
        val sortOrder = SortOrder.values().map { it.name.capitalizeWords() }.toTypedArray()

        (textInputLayoutSortBy.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(sortBy)
        (textInputLayoutSortOrder.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            sortOrder
        )
    }

    private fun MenuSortBinding.setView() {
        val it = args.sort
        textInputLayoutSortBy.editText?.setText(it.sortBy.name.capitalizeWords())
        textInputLayoutSortOrder.editText?.setText(it.sortOrder.name.capitalizeWords())
    }

}