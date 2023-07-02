package com.atech.course

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.CourseDetail
import com.atech.core.utils.DataState
import com.atech.course.databinding.FragmentCourseBinding
import com.atech.theme.Axis
import com.atech.theme.ParentActivity
import com.atech.theme.ToastLength
import com.atech.theme.addViews
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.launchWhenStarted
import com.atech.theme.navigate
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CourseFragment : Fragment(R.layout.fragment_course) {
    private val binding: FragmentCourseBinding by viewBinding()

    private val mainActivity: ParentActivity by lazy {
        requireActivity() as ParentActivity
    }

    @Inject
    lateinit var cases: ApiCases


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            openDrawer()
        }
        loadData()
        handleBackPress()
    }

    private fun loadData() = launchWhenStarted {
        cases.course.invoke().collectLatest { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    toast(dataState.exception.message.toString(), ToastLength.LONG)
                }

                is DataState.Success -> {
                    binding.bindUi(dataState.data)
                }

                else -> Unit
            }
        }
    }

    private fun FragmentCourseBinding.bindUi(course: List<CourseDetail>) = this.llCourse.run {
        removeAllViews()
        course.forEach { s ->
            addViews(requireActivity(), R.layout.row_course, s) { course, view ->
                view.findViewById<TextView>(R.id.tv_course_name).text = course.courseName
                view.rootView.apply {
                    setOnClickListener {
                        navigateToSemChoose(course.courseName, course.totalSemester)
                    }
                }
            }
        }
    }

    private fun navigateToSemChoose(courseName: String, totalSemester: Int) {
        exitTransition(Axis.X)
        val action = CourseFragmentDirections.actionCourseFragmentToSemChooseFragment(
            courseName,
            totalSemester
        )
       navigate(action)
    }

    private fun FragmentCourseBinding.openDrawer() =
        this.materialButtonMenu.setOnClickListener {
            mainActivity.toggleDrawer()
        }

    private fun handleBackPress() {
        customBackPress {
            when {
                mainActivity.getDrawerLayout().isDrawerOpen(GravityCompat.START) -> {
                    mainActivity.setDrawerState(false)
                }

                else -> findNavController().navigateUp()
            }
        }
    }
}