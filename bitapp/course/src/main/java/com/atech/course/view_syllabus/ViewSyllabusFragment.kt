package com.atech.course.view_syllabus

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.core.retrofit.ApiCases
import com.atech.course.R
import com.atech.course.databinding.FragmentViewOfflineSyllabusBinding
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.view_syllabus.online.OnlineSyllabusFragment
import com.atech.syllabus.setFragment
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.set
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewSyllabusFragment : BaseFragment(R.layout.fragment_view_offline_syllabus,Axis.X) {
    private val binding: FragmentViewOfflineSyllabusBinding by viewBinding()

    private val args: ViewSyllabusFragmentArgs by navArgs()

    private val subject: SyllabusUIModel by lazy {
        args.model
    }

    @Inject
    lateinit var cases: ApiCases

    @Inject
    lateinit var syllabusUIMapper: OfflineSyllabusUIMapper

    private val onlineFragment by lazy {
        OnlineSyllabusFragment()
            .also {
                it.setPairAndCase(
                    subject.subject to args.courseSem, cases
                )
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            root.transitionName = subject.subject
            setToolbar()
            if (subject.isFromOnline) {
                setOnlineSyllabus()
                return@apply
            }
            setOfflineSyllabus()
        }
    }

    private fun FragmentViewOfflineSyllabusBinding.setOnlineSyllabus() = this.loadSubject.apply {
        childFragmentManager.beginTransaction().replace(R.id.load_subject, onlineFragment).commit()
    }

    private fun FragmentViewOfflineSyllabusBinding.setOfflineSyllabus() = this.loadSubject.apply {
        setFragment(R.id.load_subject, childFragmentManager, syllabusUIMapper.mapToEntity(subject))
    }

    private fun FragmentViewOfflineSyllabusBinding.setToolbar() = this.includeToolbar.apply {
        set(ToolbarData(titleString = subject.subject, action = {
            findNavController().navigateUp()
        }))
    }
}