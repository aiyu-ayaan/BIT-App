package com.atech.attendance

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.attendance.databinding.FragmentAttendanceBinding
import com.atech.theme.enterTransition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private val binding: FragmentAttendanceBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setFab()
        }
    }

    private fun FragmentAttendanceBinding.setFab() = this.extendedFab.setOnClickListener {
        navigateToAttendance()
    }

    private fun navigateToAttendance() {
        val action =
            AttendanceFragmentDirections.actionAttendanceFragmentToAddEditAttendanceBottomSheet()
        findNavController().navigate(action)
    }

}