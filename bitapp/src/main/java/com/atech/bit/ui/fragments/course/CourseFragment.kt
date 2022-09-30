package com.atech.bit.ui.fragments.course

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentCourseBinding
import com.atech.core.utils.Course
import com.atech.core.utils.KEY_TOGGLE_SYLLABUS_SOURCE
import com.atech.core.utils.RemoteConfigUtil
import com.atech.core.utils.TAG
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CourseFragment : Fragment(R.layout.fragment_course) {

    private val binding: FragmentCourseBinding by viewBinding()

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var remoteConfigUtil: RemoteConfigUtil

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (myScrollViewerInstanceState != null) {
            binding.nestedViewSyllabus.onRestoreInstanceState(myScrollViewerInstanceState)
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.apply {
            imageButtonBba.transitionName = Course.Bba.name
            imageButtonBca.transitionName = Course.Bca.name
            imageButtonBca.setOnClickListener {
                navigateToSemChoose(Course.Bca.name, imageButtonBca)
            }
            imageButtonBba.setOnClickListener {
                navigateToSemChoose(Course.Bba.name, imageButtonBba)
            }
            textViewBca.setOnClickListener {
                navigateToSemChoose(Course.Bca.name, imageButtonBca)
            }

            textViewBba.setOnClickListener {
                navigateToSemChoose(Course.Bba.name, imageButtonBba)
            }
        }
        setDefaultValueForSwitch()
    }

    private fun setDefaultValueForSwitch() {
        remoteConfigUtil.fetchData({
            Log.e(TAG, "setDefaultValueForSwitch: $it")
        }) {
            val isSwitchOn = remoteConfigUtil.getBoolean(KEY_TOGGLE_SYLLABUS_SOURCE)
            pref.edit()
                .putBoolean(KEY_TOGGLE_SYLLABUS_SOURCE, isSwitchOn)
                .apply()
        }
    }


    override fun onPause() {
        super.onPause()
        myScrollViewerInstanceState = binding.nestedViewSyllabus.onSaveInstanceState()
    }

    private fun navigateToSemChoose(request: String, view: ImageButton) {
        val extras = FragmentNavigatorExtras(view to request)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        try {
            val action =
                CourseFragmentDirections.actionCourseFragmentToSemChooseFragment(request)
            findNavController().navigate(action, extras)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Press one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }


    companion object {
        var myScrollViewerInstanceState: Parcelable? = null
    }

}