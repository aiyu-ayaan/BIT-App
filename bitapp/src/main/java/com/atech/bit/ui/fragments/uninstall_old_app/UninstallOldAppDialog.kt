package com.atech.bit.ui.fragments.uninstall_old_app

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.atech.bit.R
import com.atech.bit.databinding.DialogUninstallOldAppBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.core.utils.KEY_DO_NOT_SHOW_AGAIN
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UninstallOldAppDialog : DialogFragment() {

    private lateinit var binding: DialogUninstallOldAppBinding
    private val communicatorViewModel: CommunicatorViewModel by activityViewModels()

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogUninstallOldAppBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.blank))
            .setView(binding.root)
            .setPositiveButton(resources.getString(R.string.uninstall)) { _, _ ->
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:com.aatec.bit")
                startActivity(intent)
            }
            .setNegativeButton(resources.getString(R.string.may_be_next_time)) { dialog, _ ->
                communicatorViewModel.uninstallDialogSeen = true
                dialog.dismiss()
            }
            .setNeutralButton(resources.getString(R.string.do_not_show_this)) { dialog, _ ->
                pref.edit()
                    .putBoolean(KEY_DO_NOT_SHOW_AGAIN, true)
                    .apply()
                dialog.dismiss()
            }

        return dialog.create()
    }
}