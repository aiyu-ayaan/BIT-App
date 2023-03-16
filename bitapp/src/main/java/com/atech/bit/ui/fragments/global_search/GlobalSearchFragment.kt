package com.atech.bit.ui.fragments.global_search

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentGlobalSearchBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.bit.ui.fragments.global_search.adapter.SearchRecyclerViewAdapter
import com.atech.bit.ui.fragments.global_search.model.SearchItem
import com.atech.bit.utils.launchWhenCreated
import com.atech.bit.utils.setEnterAnimation
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.data.ui.events.Events
import com.atech.core.data.ui.notice.Notice3
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private const val TAG = "GlobalSearchFragment"

@AndroidEntryPoint
class GlobalSearchFragment : Fragment(R.layout.fragment_global_search) {

    private val binding: FragmentGlobalSearchBinding by viewBinding()
    private val viewModel: GlobalSearchViewModel by viewModels()
    private val communicatorViewModel: CommunicatorViewModel by activityViewModels()

    private lateinit var sAdapter: SearchRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEnterAnimation(MaterialSharedAxis.Z)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setQuery()
        binding.apply {
            setRecyclerView()
            setChips()
        }
        viewModel.searchContent.observe(viewLifecycleOwner) { items ->
            sAdapter.items = items.toMutableList()
        }

    }

    private fun FragmentGlobalSearchBinding.setRecyclerView() = this.recyclerViewSearch.apply {
        adapter = SearchRecyclerViewAdapter { v, item ->
            when (item) {
                is SearchItem.Syllabus -> setOnSyllabusClickListener(item.data, v)
                is SearchItem.Notice -> navigationToNotice3Description(item.data, v)
                is SearchItem.Event -> navigateToEventDetail(item.data, v)
                else -> {

                }
            }
        }.also { sAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun navigationToNotice3Description(notice: Notice3, view: View) {
        val extras = FragmentNavigatorExtras(view to notice.path)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = NavGraphDirections.actionGlobalNoticeDetailFragment(notice.path)
        findNavController().navigate(action, extras)
    }

    private fun setOnSyllabusClickListener(syllabusModel: SyllabusModel, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        activity?.findViewById<EditText>(R.id.searchInput)?.clearFocus()
        try {
            val extras = FragmentNavigatorExtras(view to syllabusModel.openCode)
            val action =
                NavGraphDirections.actionGlobalSubjectHandlerFragment(
                    syllabusModel
                )
            findNavController().navigate(action, extras)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Press one item at a time !!l", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setQuery() = launchWhenCreated {
        communicatorViewModel.query.collectLatest {
            viewModel.query.value = if (it == "") "none" else it
        }
    }

    private fun FragmentGlobalSearchBinding.setChips() = this.apply {
        chipGroupSearch.setOnCheckedStateChangeListener { group, checkedIds ->
            checkedIds.forEach { id ->
                val chip = group.findViewById<View>(id)
                when (chip.id) {
                    R.id.chip_all ->
                        viewModel.filterState.value = GlobalSearchViewModel.FilterState()


                    R.id.chip_syllabus ->
                        viewModel.filterState.value = GlobalSearchViewModel.FilterState(
                            syllabus = true,
                            holiday = false, notice = false, event = false
                        )


                    R.id.chip_holiday ->
                        viewModel.filterState.value = GlobalSearchViewModel.FilterState(
                            syllabus = false,
                            holiday = true, notice = false, event = false
                        )


                    R.id.chip_notice ->
                        viewModel.filterState.value = GlobalSearchViewModel.FilterState(
                            syllabus = false,
                            holiday = false, notice = true, event = false
                        )


                    R.id.chip_event ->
                        viewModel.filterState.value = GlobalSearchViewModel.FilterState(
                            syllabus = false,
                            holiday = false, notice = false, event = true
                        )

                }
            }
        }
    }

    private fun navigateToEventDetail(event: Events, view: View) {
        val extras = FragmentNavigatorExtras(view to event.path)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = NavGraphDirections.actionGlobalEventDetailFragment(path = event.path)
        findNavController().navigate(action, extras)
    }

    override fun onPause() {
        super.onPause()
        communicatorViewModel.openFirst = false
    }

    override fun onDestroy() {
        super.onDestroy()
        communicatorViewModel.query.value = "none"
        communicatorViewModel.openFirst = true
    }
}