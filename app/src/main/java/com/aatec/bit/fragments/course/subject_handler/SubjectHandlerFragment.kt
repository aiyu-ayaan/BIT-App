package com.aatec.bit.fragments.course.subject_handler

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentSubjectHandlerBinding
import com.aatec.bit.utils.changeStatusBarToolbarColor
import com.aatec.syllabus.setFragment
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectHandlerFragment : Fragment(R.layout.fragment_subject_handler) {
    private val binding: FragmentSubjectHandlerBinding by viewBinding()
    private val args: SubjectHandlerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            duration = resources.getInteger(R.integer.duration_small).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.transitionName = args.syllabus.openCode
        setFragment(R.id.load_subject, childFragmentManager, args.syllabus)
        setHasOptionsMenu(true)
        detectScroll()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        binding.scrollViewSubjectHandler.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            when (scrollY) {
                0 -> {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        com.google.android.material.R.attr.colorSurface
                    )
                }
                else -> {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        R.attr.bottomBar
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.changeStatusBarToolbarColor(
            R.id.toolbar,
            com.google.android.material.R.attr.colorSurface
        )
    }
}