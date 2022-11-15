package com.atech.bit.ui.fragments.course.sem_choose

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentSemChooseBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.ui.fragments.course.sem_choose.adapters.SubjectAdapter
import com.atech.bit.ui.fragments.course.sem_choose.adapters.SyllabusOnlineAdapter
import com.atech.bit.utils.addMenuHost
import com.atech.bit.utils.loadAdds
import com.atech.bit.utils.openBugLink
import com.atech.core.api.syllabus.Semester
import com.atech.core.api.syllabus.SubjectModel
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.utils.DataState
import com.atech.core.utils.KEY_TOGGLE_SYLLABUS_SOURCE
import com.atech.core.utils.RemoteConfigUtil
import com.atech.core.utils.openCustomChromeTab
import com.atech.core.utils.showSnackBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject


@AndroidEntryPoint
class SemChooseFragment : Fragment(R.layout.fragment_sem_choose) {

    private val viewModel: ChooseSemViewModel by viewModels()
    private val prefManagerViewModel: PreferenceManagerViewModel by activityViewModels()

    private val binding: FragmentSemChooseBinding by viewBinding()
    private lateinit var courseTheoryAdapter: SubjectAdapter
    private lateinit var courseLabAdapter: SubjectAdapter
    private lateinit var coursePeAdapter: SubjectAdapter
    private lateinit var onlineTheoryAdapter: SyllabusOnlineAdapter
    private lateinit var onlineLabAdapter: SyllabusOnlineAdapter
    private lateinit var onlinePEAdapter: SyllabusOnlineAdapter
    private var courseSem: String = ""

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var remoteConfigUtil: RemoteConfigUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            duration = resources.getInteger(R.integer.duration_medium).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    @Inject
    lateinit var db: FirebaseFirestore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        restoreScroll(binding)
        binding.root.transitionName = viewModel.request


        onlineTheoryAdapter = SyllabusOnlineAdapter { pos ->
            navigateToViewOnlineSyllabus(pos)
        }
        onlineLabAdapter = SyllabusOnlineAdapter {
            navigateToViewOnlineSyllabus(it)
        }.also { it.setType("Lab") }
        onlinePEAdapter = SyllabusOnlineAdapter {
            navigateToViewOnlineSyllabus(it)
        }.also { it.setType("Pe") }

        binding.semChoseOnlineExt.recyclerViewOnlineSyllabus.apply {
            adapter = ConcatAdapter(onlineTheoryAdapter, onlineLabAdapter, onlinePEAdapter)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecorationNoLast(
                requireContext(), LinearLayoutManager.VERTICAL
            ).apply {
                setDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.divider
                    )
                )
            })
        }

        offlineDataSource()
        buttonClick()
        setUpMenu()
        switchClick()
        setSource()
        getOnlineSyllabus()
        setAds()

    }

    private fun navigateToViewOnlineSyllabus(model: SubjectModel) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        val action =
            SemChooseFragmentDirections.actionSemChooseFragmentToViewSyllabusFragment(
                model.subjectName,
                courseSem
            )
        findNavController().navigate(action)
    }

    private fun setAds() {
        requireContext().loadAdds(binding.adView)
    }


    private fun offlineDataSource() {
        courseTheoryAdapter = SubjectAdapter { syllabusModel, view ->
            syllabusClick(syllabusModel, view)
        }
        courseTheoryAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        courseLabAdapter = SubjectAdapter { syllabusModel, view ->
            syllabusClick(syllabusModel, view)
        }
        courseLabAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        coursePeAdapter = SubjectAdapter { syllabusModel, view ->
            syllabusClick(syllabusModel, view)
        }
        coursePeAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.apply {
            semChoseExt.apply {
                showTheory.apply {
                    adapter = courseTheoryAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(DividerItemDecorationNoLast(
                        requireContext(), LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.divider
                            )
                        )
                    })
                }
                showLab.apply {
                    adapter = courseLabAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(DividerItemDecorationNoLast(
                        requireContext(), LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.divider
                            )
                        )
                    })
                }
                showPe.apply {
                    adapter = coursePeAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(DividerItemDecorationNoLast(
                        requireContext(), LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.divider
                            )
                        )
                    })
                }
            }
        }
        viewModel.theory.observe(viewLifecycleOwner) {
            courseTheoryAdapter.submitList(it)
        }
        viewModel.lab.observe(viewLifecycleOwner) {
            binding.semChoseExt.showLab.isVisible = it.isNotEmpty()
            binding.semChoseExt.textView7.isVisible = it.isNotEmpty()
            courseLabAdapter.submitList(it)
        }
        viewModel.pe.observe(viewLifecycleOwner) {
            binding.semChoseExt.showPe.isVisible = it.isNotEmpty()
            binding.semChoseExt.textView8.isVisible = it.isNotEmpty()
            coursePeAdapter.submitList(it)
        }
        prefManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            viewModel.sem.value = "${viewModel.request}${it.semSyllabus}"
            courseSem = "${viewModel.request}${it.semSyllabus}".lowercase()
            buttonColorChange(it.semSyllabus, binding)
        }
    }

    private fun getOnlineSyllabus() {
        viewModel.getOnlineSyllabus().observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                DataState.Empty -> {}
                is DataState.Error -> {
                    Log.d("AAA", "getOnlineSyllabus: ${dataState.exception}")
                    if (dataState.exception is HttpException) {
                        binding.root.showSnackBar(
                            "${dataState.exception.message}", Snackbar.LENGTH_SHORT, "Report"
                        ) {
                            setViewOfOnlineSyllabusExt(false)
                            requireActivity().openBugLink(
                                com.atech.core.R.string.bug_repost,
                                "${this@SemChooseFragment.javaClass.simpleName}.class",
                                dataState.exception.message
                            )
                        }
                    }

                }

                DataState.Loading -> {
                    binding.semChoseOnlineExt.progressBarLoading.isVisible = true
                    binding.semChoseOnlineExt.noData.isVisible = false
                    binding.semChoseOnlineExt.noDataText.isVisible = false
                }

                is DataState.Success -> {
                    Log.d("XXX", "getOnlineSyllabus: ${dataState.data}")
                    dataState.data.semester?.let { syllabus ->
                        setOnLineData(syllabus)
                    }
                    setViewOfOnlineSyllabusExt(dataState.data.semester != null)
                }
            }
        }
    }

    private fun setViewOfOnlineSyllabusExt(isVisible: Boolean) {
        binding.semChoseOnlineExt.progressBarLoading.isVisible = false
        binding.semChoseOnlineExt.noData.isVisible = !isVisible
        binding.semChoseOnlineExt.noDataText.isVisible = !isVisible
        binding.semChoseOnlineExt.recyclerViewOnlineSyllabus.isVisible = isVisible
        binding.semChoseOnlineExt.textView6.isVisible = isVisible
    }

    private fun setOnLineData(data: Semester) {
        onlineTheoryAdapter.submitList(data.subjects.theory)
        onlineLabAdapter.setStartPos(data.subjects.theory.size)
        onlineLabAdapter.submitList(data.subjects.lab)
        onlinePEAdapter.setStartPos(data.subjects.theory.size + data.subjects.lab.size)

        onlinePEAdapter.submitList(data.subjects.pe)
    }

    private fun setSource() {
        val source = pref.getBoolean(KEY_TOGGLE_SYLLABUS_SOURCE, false)
        binding.switchOldNew.isChecked = source
        setText(source)
        layoutChanges(source)
    }

    private fun switchClick() = binding.switchOldNew.apply {
        setOnCheckedChangeListener { _, isChecked ->
            saveSource(isChecked)
            setText(isChecked)
            layoutChanges(isChecked)
        }
    }

    private fun setText(isEnable: Boolean) {
        binding.switchOldNew.text = if (isEnable) resources.getString(R.string.switch_to_old)
        else resources.getString(R.string.switch_to_new)
    }

    private fun saveSource(isEnable: Boolean) {
        pref.edit().putBoolean(
            KEY_TOGGLE_SYLLABUS_SOURCE, isEnable
        ).apply()
    }

    private fun layoutChanges(isEnable: Boolean) = binding.apply {
        semChoseOnlineExt.root.isVisible = isEnable
        semChoseExt.root.isVisible = !isEnable
    }


    private fun setUpMenu() {
        addMenuHost(R.menu.menu_sem_choose) { item ->
            when (item.itemId) {
                R.id.menu_download_syllabus -> {
                    getSyllabus(viewModel.request?.uppercase() ?: "BCA")
                    true
                }

                else -> false
            }
        }
    }


    private fun buttonClick() {
        binding.apply {
            bt1.setOnClickListener { prefManagerViewModel.updateSemSyllabus("1") }
            bt2.setOnClickListener { prefManagerViewModel.updateSemSyllabus("2") }
            bt3.setOnClickListener { prefManagerViewModel.updateSemSyllabus("3") }
            bt4.setOnClickListener { prefManagerViewModel.updateSemSyllabus("4") }
            bt5.setOnClickListener { prefManagerViewModel.updateSemSyllabus("5") }
            bt6.setOnClickListener { prefManagerViewModel.updateSemSyllabus("6") }
        }
    }

    private fun syllabusClick(syllabusModel: SyllabusModel, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_small).toLong()
        }

        try {
            val extras = FragmentNavigatorExtras(view to syllabusModel.openCode)
            val action = NavGraphDirections.actionGlobalSubjectHandlerFragment(
                syllabusModel
            )
            findNavController().navigate(action, extras)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Press one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun getSyllabus(course: String) {
        remoteConfigUtil.fetchData({}) {
            val link = remoteConfigUtil.getString("SYLLABUS_$course")
            requireActivity().openCustomChromeTab(link)
        }
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun restoreScroll(binding: FragmentSemChooseBinding) {
        try {
            if (viewModel.chooseSemNestedViewPosition != null) binding.semChoseExt.nestedViewSyllabus.post {
                binding.semChoseExt.nestedViewSyllabus.scrollTo(
                    0, viewModel.chooseSemNestedViewPosition!!
                )
            }
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    override fun onStop() {
        try {
            super.onStop()
            viewModel.chooseSemNestedViewPosition = binding.semChoseExt.nestedViewSyllabus.scrollY
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    private fun buttonColorChange(request: String, binding: FragmentSemChooseBinding) {
        when (request) {
            "1" -> binding.toggleChip.check(R.id.bt1)
            "2" -> binding.toggleChip.check(R.id.bt2)
            "3" -> binding.toggleChip.check(R.id.bt3)
            "4" -> binding.toggleChip.check(R.id.bt4)
            "5" -> binding.toggleChip.check(R.id.bt5)
            "6" -> binding.toggleChip.check(R.id.bt6)
            else -> Log.d("Error", "buttonColorChange: Error")
        }
    }
}