package com.atech.bit.ui.fragments.course

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentCourseBinding
import com.atech.bit.utils.addViews
import com.atech.bit.utils.setExitShareAxisTransition
import com.atech.core.api.ApiRepository
import com.atech.core.utils.DataState
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
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var api: ApiRepository

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
            setUi()

        }
    }

    private fun setUi() {
        api.fetchCourse().asLiveData().observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                DataState.Empty -> {}
                is DataState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        dataState.exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                DataState.Loading -> {}
                is DataState.Success -> {
                    bindUi(dataState.data)
                }
            }
        }
    }

    private fun bindUi(course: List<String>) = binding.llCourse.run {
        course.forEach { s ->
            addViews(requireActivity(), R.layout.row_course, s) { course, view ->
                view.findViewById<TextView>(R.id.tv_course_name).text = course
                view.rootView.apply {
                    setOnClickListener {
                        navigateToSemChoose(course)
                    }
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        myScrollViewerInstanceState = binding.nestedViewSyllabus.onSaveInstanceState()
    }

    private fun navigateToSemChoose(request: String) {
        setExitShareAxisTransition()
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