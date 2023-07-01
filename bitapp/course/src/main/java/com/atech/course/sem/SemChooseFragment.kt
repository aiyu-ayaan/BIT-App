package com.atech.course.sem

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.core.utils.SharePref
import com.atech.course.R
import com.atech.course.databinding.FragmentSemChooseBinding
import com.atech.course.utils.tabSelectedListener
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.set
import com.atech.theme.toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SemChooseFragment : Fragment(R.layout.fragment_sem_choose) {

    private val binding: FragmentSemChooseBinding by viewBinding()
    private val args: SemChooseFragmentArgs by navArgs()

    @Inject
    lateinit var pref: SharedPreferences

    private inline var lastChooseSem: Int
        get() = pref.getInt(SharePref.ChooseSemLastSelectedSem.name, 0).let {
            if (it > args.sem) args.sem
            else it
        }
        set(value) = pref.edit().putInt(SharePref.ChooseSemLastSelectedSem.name, value).apply()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            setToolbar()
            setUpTabLayout()
        }
    }

    private fun FragmentSemChooseBinding.setUpTabLayout() {
        (1..args.sem).map { "Semester $it" }.map {
            tabLayoutSemChoose.newTab().apply { text = it }
        }.also { tabLayoutSemChoose.addView(it) }
        tabLayoutSemChoose.getTabAt(lastChooseSem - 1)?.select()
        tabLayoutSemChoose.tabSelectedListener { tab ->
            try {
                tab?.text.toString().replace("Semester ", "").trim().toInt()
                    .also { lastChooseSem = it }
            } catch (e: Exception) {
                toast("Something went wrong to save last selected sem")
            }
        }
    }

    private fun TabLayout.addView(tabs: List<Tab>) {
        tabs.forEach { addTab(it) }
    }


    private fun FragmentSemChooseBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                titleString = "${args.request} Sem $lastChooseSem",
                action = findNavController()::navigateUp
            )
        )
    }
}