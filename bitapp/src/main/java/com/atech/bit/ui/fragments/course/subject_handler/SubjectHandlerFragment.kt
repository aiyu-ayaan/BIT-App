package com.atech.bit.ui.fragments.course.subject_handler

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.FragmentSubjectHandlerBinding
import com.atech.bit.utils.addMenuHost
import com.atech.bit.utils.openShareDeepLink
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.utils.SHARE_TYPE_SYLLABUS
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.syllabus.setFragment
import com.atech.syllabus.setNoSyllabusFragment
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubjectHandlerFragment : Fragment(R.layout.fragment_subject_handler) {
    private val binding: FragmentSubjectHandlerBinding by viewBinding()
    private val args: SubjectHandlerFragmentArgs by navArgs()
    private var subject: SyllabusModel? = null

    @Inject
    lateinit var syllabusDao: SyllabusDao


    @Inject
    lateinit var pref: SharedPreferences

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

        //For Deep Link
        forDeepLink()

//        For Normal navigation
        forSyllabusFromChooseSemFragment()

        detectScroll()
        addingMenuHost()
    }

    private fun forSyllabusFromChooseSemFragment() {
        args.syllabus?.let {
            subject = it
            binding.root.transitionName = it.openCode
            setFragment(R.id.load_subject, childFragmentManager, it)
        }
    }

    private fun addingMenuHost() {
        addMenuHost(R.menu.notice_description_menu) { it ->
            when (it.itemId) {
                R.id.menu_notice_share -> {
                    subject?.let { subject ->
                        requireActivity().openShareDeepLink(
                            subject.subject,
                            subject.openCode,
                            SHARE_TYPE_SYLLABUS
                        )
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun forDeepLink() = lifecycleScope.launchWhenStarted {
        if (args.openCode.isNotBlank()) {
            subject = syllabusDao.getSyllabusDeepLink(args.openCode)
            if (subject != null)
                setFragment(R.id.load_subject, childFragmentManager, subject!!)
            else
                setNoSyllabusFragment(R.id.load_subject, childFragmentManager)
        }
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

}