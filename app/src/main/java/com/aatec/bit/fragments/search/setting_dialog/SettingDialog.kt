package com.aatec.bit.fragments.search.setting_dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.DialogSearchSettingBinding
import com.aatec.core.utils.showSnackBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingDialog : DialogFragment() {
    private lateinit var binding: DialogSearchSettingBinding
    private val prefManager: PreferenceManagerViewModel by activityViewModels()
    private val args: SettingDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSearchSettingBinding.inflate(layoutInflater)
        binding.apply {
            setView()
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.search_setting))
            .setView(binding.root)
            .setPositiveButton(resources.getString(R.string.save)) { dialog, which ->
                binding.apply {
                    saveSettingInDataStore(
                        checkEvent.isChecked,
                        checkHoliday.isChecked,
                        checkNotice.isChecked,
                        checkSubject.isChecked,
                    )
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->

            }

        return dialog.create()
    }

    private fun saveSettingInDataStore(
        event: Boolean,
        holiday: Boolean,
        notice: Boolean,
        syllabus: Boolean
    ) {
        prefManager.updateSearchSetting(event, holiday, notice, syllabus)
        activity?.findViewById<RelativeLayout>(R.id.search_root_layout)
            ?.showSnackBar(resources.getString(R.string.done), Snackbar.LENGTH_SHORT)
        dismiss()
    }

    private fun setView() {
        binding.apply {
            checkEvent.isChecked = args.searchPreference.event
            checkHoliday.isChecked = args.searchPreference.holiday
            checkNotice.isChecked = args.searchPreference.notice
            checkSubject.isChecked = args.searchPreference.subject
        }
    }
}