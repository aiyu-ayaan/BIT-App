package com.atech.course.sem

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.SharePref
import com.atech.course.R
import com.atech.course.databinding.FragmentSemChooseBinding
import com.atech.course.sem.adapter.CourseAdapter
import com.atech.course.sem.adapter.CourseItem
import com.atech.course.sem.adapter.SyllabusUIMapper
import com.atech.course.utils.tabSelectedListener
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.launchWhenStarted
import com.atech.theme.set
import com.atech.theme.toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@AndroidEntryPoint
class SemChooseFragment : Fragment(R.layout.fragment_sem_choose) {

    private val binding: FragmentSemChooseBinding by viewBinding()
    private val args: SemChooseFragmentArgs by navArgs()
    private lateinit var courseAdapter: CourseAdapter


    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var dao: SyllabusDao

    @Inject
    lateinit var offlineSyllabusMapper: SyllabusUIMapper

    private inline var lastChooseSem: Int
        get() = pref.getInt(SharePref.ChooseSemLastSelectedSem.name, 0).let {
            if (it > args.sem) args.sem
            else it
        }
        set(value) = pref.edit().putInt(SharePref.ChooseSemLastSelectedSem.name, value).apply()


    private val sem = MutableStateFlow("")
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
            setRecyclerView()
        }
        observeData()
    }

    private fun FragmentSemChooseBinding.setRecyclerView() = this.recyclerViewSemChoose.apply {
        adapter = CourseAdapter().also { courseAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() = launchWhenStarted {
        sem.flatMapLatest { sem ->
            combine(
                dao.getSyllabusType("${args.request}$sem", "Theory"),
                dao.getSyllabusType("${args.request}$sem", "Lab"),
                dao.getSyllabusType("${args.request}$sem", "PE")
            ) { theory, lab, pe ->
                Triple(theory, lab, pe)
            }
        }.collectLatest { (theory, lab, pe) ->
            val list = mutableListOf<CourseItem>()
            if(theory.isNotEmpty()) {
                list.add(CourseItem.Title(getString(com.atech.theme.R.string.theory)))
                list.addAll(theory.map { CourseItem.Subject(offlineSyllabusMapper.mapFormEntity(it)) })
            }
            if (lab.isNotEmpty()) {
                list.add(CourseItem.Title(getString(com.atech.theme.R.string.lab)))
                list.addAll(lab.map { CourseItem.Subject(offlineSyllabusMapper.mapFormEntity(it)) })
            }
            if (pe.isNotEmpty()) {
                list.add(CourseItem.Title(getString(com.atech.theme.R.string.pe)))
                list.addAll(pe.map { CourseItem.Subject(offlineSyllabusMapper.mapFormEntity(it)) })
            }
            courseAdapter.item = list
        }
    }

    private fun FragmentSemChooseBinding.setUpTabLayout() = this.apply {
        sem.value = lastChooseSem.toString()
        (1..args.sem).map { "Semester $it" }.map {
            tabLayoutSemChoose.newTab().apply { text = it }
        }.also { tabLayoutSemChoose.addView(it) }
        tabLayoutSemChoose.getTabAt(lastChooseSem - 1)?.select()
        tabLayoutSemChoose.tabSelectedListener { tab ->
            try {
                tab?.text.toString().replace("Semester ", "").trim().toInt()
                    .also {
                        sem.value = it.toString()
                        lastChooseSem = it
                    }
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