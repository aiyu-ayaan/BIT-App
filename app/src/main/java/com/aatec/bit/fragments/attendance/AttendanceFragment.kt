package com.aatec.bit.fragments.attendance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aatec.bit.R
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {


    @Inject
    lateinit var db: FirebaseFirestore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}