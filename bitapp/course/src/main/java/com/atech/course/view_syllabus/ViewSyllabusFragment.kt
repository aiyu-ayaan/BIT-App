package com.atech.course.view_syllabus

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.BITApiClient.Companion.BASE_URL
import com.atech.core.utils.encodeUrlSpaces
import com.atech.course.R
import com.atech.course.databinding.FragmentViewOfflineSyllabusBinding
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.view_syllabus.online.OnlineSyllabusFragment
import com.atech.syllabus.setFragment
import com.atech.theme.AdsUnit
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.openShareString
import com.atech.theme.set
import com.atech.theme.setAdsUnit
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class ViewSyllabusFragment : BaseFragment(R.layout.fragment_view_offline_syllabus, Axis.X) {
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
            loadAds()
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

    private fun FragmentViewOfflineSyllabusBinding.loadAds() = this.includeAdsView.apply {
        setAdsUnit(AdsUnit.Miscellaneous)
    }

    private fun FragmentViewOfflineSyllabusBinding.setToolbar() = this.includeToolbar.apply {
        set(
            if (subject.isFromOnline)
                ToolbarData(titleString = subject.subject, action = {
                    findNavController().navigateUp()
                }, endIcon = com.atech.theme.R.drawable.ic_share, endAction = {
                    share(subject.subject to args.courseSem)
                })
            else
                ToolbarData(titleString = subject.subject, action = {
                    findNavController().navigateUp()
                })
        )
    }

    private fun share(pair: Pair<String, String>) {
        toast("Getting Web Link")
        val url = URL(
            "${BASE_URL}syllabus/${
                pair.second.replace(
                    "\\d".toRegex(),
                    ""
                )
            }/${pair.second}/subjects/${pair.first}".encodeUrlSpaces()
        )
        val text = """
            ${pair.first} .
            Link: $url
            
            Sauce: ${resources.getString(com.atech.theme.R.string.play_store_link)}${requireActivity().packageName}
        """.trimIndent()
        requireActivity().openShareString(text)
    }
}