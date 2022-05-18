package com.aatec.bit.ui.fragments.course

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageButton
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentCourseBinding
import com.aatec.core.utils.Course
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
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
        val action =
            CourseFragmentDirections.actionCourseFragmentToSemChooseFragment(request)
        findNavController().navigate(action, extras)
//        try {
//
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Press one item at a time !!", Toast.LENGTH_SHORT)
//                .show()
//        }
    }


    companion object {
        var myScrollViewerInstanceState: Parcelable? = null
    }

}