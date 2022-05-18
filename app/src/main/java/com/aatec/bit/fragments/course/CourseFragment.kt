package com.aatec.bit.fragments.course

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.CommunicatorViewModel
import com.aatec.bit.activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.FragmentCourseBinding
import com.aatec.bit.fragments.course.sem_choose.ChooseSemViewModel
import com.aatec.core.data.room.syllabus.SyllabusModel
import com.aatec.core.utils.Course
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
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.apply {
            buttonBba.setOnClickListener {
                navigateToSemChoose(Course.Bba.name)
            }
            buttonBca.setOnClickListener {
                navigateToSemChoose(Course.Bca.name)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        myScrollViewerInstanceState = binding.nestedViewSyllabus.onSaveInstanceState()
    }

    private fun navigateToSemChoose(request: String) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        try {
            val action =
                CourseFragmentDirections.actionCourseFragmentToSemChooseFragment(request)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Press one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }



    companion object {
        var myScrollViewerInstanceState: Parcelable? = null
    }

}