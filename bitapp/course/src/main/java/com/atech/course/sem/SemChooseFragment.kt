package com.atech.course.sem

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.DataState
import com.atech.core.utils.NetworkBoundException
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.atech.course.CourseFragmentDirections
import com.atech.course.R
import com.atech.course.databinding.FragmentSemChooseBinding
import com.atech.course.sem.adapter.CourseAdapter
import com.atech.course.sem.adapter.CourseItem
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.utils.SyllabusEnableModel
import com.atech.course.utils.compareToCourseSem
import com.atech.course.utils.tabSelectedListener
import com.atech.theme.Axis
import com.atech.theme.ParentActivity
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.exitSharedElementTransform
import com.atech.theme.exitTransition
import com.atech.theme.launchWhenCreated
import com.atech.theme.launchWhenStarted
import com.atech.theme.navigate
import com.atech.theme.openBugLink
import com.atech.theme.set
import com.atech.theme.showSnackBar
import com.atech.theme.toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@AndroidEntryPoint
class SemChooseFragment : Fragment(R.layout.fragment_sem_choose) {

    private val binding: FragmentSemChooseBinding by viewBinding()
    private val args: SemChooseFragmentArgs by navArgs()
    private lateinit var courseAdapter: CourseAdapter

    private val isEnableStateFlow = MutableStateFlow(false)

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var dao: SyllabusDao

    @Inject
    lateinit var offlineSyllabusMapper: OfflineSyllabusUIMapper

    @Inject
    lateinit var onlineSyllabusUIMapper: OnlineSyllabusUIMapper

    @Inject
    lateinit var cases: ApiCases

    private val mainActivity: ParentActivity by lazy {
        requireActivity() as ParentActivity
    }
    private inline var lastChooseSem: Int
        get() = pref.getInt(SharePrefKeys.ChooseSemLastSelectedSem.name, 0).let {
            if (it > args.sem) args.sem
            else it
        }
        set(value) = pref.edit().putInt(SharePrefKeys.ChooseSemLastSelectedSem.name, value).apply()
            .also {
                isEnableStateFlow.value =
                    syllabusEnableModel.compareToCourseSem("${args.request}$value")
            }

    private val syllabusEnableModel: SyllabusEnableModel by lazy {
        pref.getString(
            SharePrefKeys.KeyToggleSyllabusSource.name,
            getString(com.atech.theme.R.string.def_value_online_syllabus)
        )?.let {
            fromJSON(it, SyllabusEnableModel::class.java)
        } ?: fromJSON(
            getString(com.atech.theme.R.string.def_value_online_syllabus),
            SyllabusEnableModel::class.java
        )
    }


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
        adapter = CourseAdapter(::navigateToViewSyllabus).also { courseAdapter = it }
        courseAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
    }

    private fun navigateToViewSyllabus(syllabus: SyllabusUIModel) {
        exitTransition(Axis.X)
        val action = CourseFragmentDirections.actionGlobalViewSyllabusFragment(
            syllabus,
            (args.request + sem.value).lowercase(),
        )
       navigate(action)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() = launchWhenCreated {
        sem.flatMapLatest { sem ->
            isEnableStateFlow.flatMapLatest { isEnable ->
                if (isEnable) onlineDataFlow("${args.request}$sem".lowercase())
                else offlineDataFlow("${args.request}$sem".lowercase())
            }
        }.collectLatest { (theory, lab, pe) ->
            val list = mutableListOf<CourseItem>()
            if (theory.isNotEmpty()) {
                list.add(CourseItem.Title(getString(com.atech.theme.R.string.theory)))
                list.addAll(theory.map { CourseItem.Subject(it) })
            }
            if (lab.isNotEmpty()) {
                list.add(CourseItem.Title(getString(com.atech.theme.R.string.lab)))
                list.addAll(lab.map { CourseItem.Subject(it) })
            }
            if (pe.isNotEmpty()) {
                list.add(CourseItem.Title(getString(com.atech.theme.R.string.pe)))
                list.addAll(pe.map { CourseItem.Subject(it) })
            }
            binding.includeNoData.lvNoData.isVisible = list.isEmpty()
            courseAdapter.items = list
        }
    }

    private fun emptyTriple(): Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>> =
        Triple(emptyList(), emptyList(), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun onlineDataFlow(courseSem: String): Flow<Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>> =
        cases.syllabus.invoke(courseSem).flatMapLatest { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    flowOf(emptyTriple())
                }

                is DataState.Success -> {
                    val theory = dataState.data.semester?.subjects?.theory?.map {
                        onlineSyllabusUIMapper.mapFormEntity(it)
                    } ?: emptyList()
                    val lab = dataState.data.semester?.subjects?.lab?.map {
                        onlineSyllabusUIMapper.mapFormEntity(it)
                    } ?: emptyList()
                    val pe = dataState.data.semester?.subjects?.pe?.map {
                        onlineSyllabusUIMapper.mapFormEntity(it)
                    } ?: emptyList()
                    flowOf(Triple(theory, lab, pe))
                }

                else -> {
                    if (dataState is DataState.Error) {
                        when (dataState.exception) {
                            // HTTP 504 Unsatisfiable Request (only-if-cached)
                            is NetworkBoundException -> {
                                when (dataState.exception as NetworkBoundException) {
                                    NetworkBoundException.NoInternet -> binding.includeNoData.tvNoData.text =
                                        getString(com.atech.theme.R.string.no_internet)

                                    NetworkBoundException.NotFound -> binding.includeNoData.tvNoData.text =
                                        getString(com.atech.theme.R.string.no_data)

                                    is NetworkBoundException.Unknown -> reportBug(dataState)
                                }
                            }

                            else -> {
                                reportBug(dataState)
                            }
                        }
                    }
                    flowOf(emptyTriple())
                }
            }
        }

    private fun reportBug(dataState: DataState.Error) {
        binding.root.showSnackBar(
            "${dataState.exception.message}", Snackbar.LENGTH_SHORT, "Report"
        ) {
            requireActivity().openBugLink(
                com.atech.theme.R.string.bug_repost,
                "${this@SemChooseFragment.javaClass.simpleName}.class",
                dataState.exception.message,
                mainActivity.getVersionName()
            )
        }
    }


    private fun offlineDataFlow(courseSem: String) = combine(
        dao.getSyllabusType(courseSem, "Theory"),
        dao.getSyllabusType(courseSem, "Lab"),
        dao.getSyllabusType(courseSem, "PE")
    ) { theory, lab, pe ->
        Triple(
            offlineSyllabusMapper.mapFromEntityList(theory),
            offlineSyllabusMapper.mapFromEntityList(lab),
            offlineSyllabusMapper.mapFromEntityList(pe)
        )
    }

    private fun FragmentSemChooseBinding.setUpTabLayout() = this.apply {
        sem.value = lastChooseSem.toString()
        (1..args.sem).map { "Semester $it" }.map {
            tabLayoutSemChoose.newTab().apply { text = it }
        }.also { tabLayoutSemChoose.addView(it) }
        tabLayoutSemChoose.getTabAt(lastChooseSem - 1)?.select()
        tabLayoutSemChoose.tabSelectedListener { tab ->
            try {
                tab?.text.toString().replace("Semester ", "").trim().toInt().also {
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
        isEnableStateFlow.value =
            syllabusEnableModel.compareToCourseSem("${args.request}$lastChooseSem")
        set(ToolbarData(titleString = "${args.request} Sem $lastChooseSem",
            action = findNavController()::navigateUp,
            switchTitle = com.atech.theme.R.string.blank,
            switchAction = {
                launchWhenStarted {
                    isEnableStateFlow.collectLatest {
                        this.isChecked = it
                    }
                }
            },
            switchOnClick = {
                isEnableStateFlow.value = it
            }))
    }

}