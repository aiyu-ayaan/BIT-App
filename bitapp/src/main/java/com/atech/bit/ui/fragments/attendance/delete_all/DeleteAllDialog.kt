package com.atech.bit.ui.fragments.attendance.delete_all

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.bit.utils.getUid
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeleteAllDialog : DialogFragment() {

    private val viewModel: DeleteAllViewModel by viewModels()
    private val userDataViewModel by viewModels<UserDataViewModel>()

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Do your really want to delete all attendance?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAll()
                if (auth.currentUser != null)
                    userDataViewModel.setAttendance(
                        getUid(auth)!!,
                        listOf(),
                        {}) {}
                dismiss()
            }.create()
}