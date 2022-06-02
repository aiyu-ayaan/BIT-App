package com.aatec.bit.ui.fragments.search

import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.aatec.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.FragmentSearchBinding
import com.aatec.bit.ui.fragments.course.CourseFragment
import com.aatec.bit.ui.fragments.event.EventAdapter
import com.aatec.bit.ui.fragments.home.adapter.HolidayHomeAdapter
import com.aatec.bit.ui.fragments.home.adapter.SyllabusHomeAdapter
import com.aatec.bit.ui.fragments.notice.Notice3Adapter
import com.aatec.core.data.preferences.SearchPreference
import com.aatec.core.data.room.syllabus.SyllabusModel
import com.aatec.core.data.ui.event.Event
import com.aatec.core.data.ui.notice.Notice3
import com.aatec.core.utils.REQUEST_ADAPTER_SEARCH
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: SearchViewModel by viewModels()
    private var isEmpty = false
    private val binding: FragmentSearchBinding by viewBinding()
    private val prefManager: PreferenceManagerViewModel by activityViewModels()
    private val communicator: CommunicatorViewModel by activityViewModels()
    private lateinit var searchPreference: SearchPreference

    @Inject
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (CourseFragment.myScrollViewerInstanceState != null) {
            binding.scrollViewSearch.onRestoreInstanceState(CourseFragment.myScrollViewerInstanceState)
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            setQuery()
            showResult.isVisible = false
            viewModel.query.asLiveData().observe(viewLifecycleOwner) {
                it?.let {
                    if (it.isBlank()) {
                        showResult.isVisible = false
                        tvLabel.text = getString(R.string.blank)
                        intro.isVisible = true
                        isEmpty = false
                    } else {
                        showResult.isVisible = true
                        intro.isVisible = false
                        isEmpty = true
                    }
                }
            }

            textShowAllHoliday.setOnClickListener {
                setOnHomeHolidayClickListener()
            }

        }

//        Settings
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            prefManager.preferencesFlow.observe(viewLifecycleOwner) { filterPreference ->
                searchPreference = filterPreference.searchPreference
                setSubjectView()
                setNoticeView()
                setHolidayView()
                setEventView()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            setSubjectView()
            setNoticeView()
            setHolidayView()
            setEventView()
        }

        setHasOptionsMenu(true)
    }

    /**
     * Set Query
     * @author Ayaan
     * @since 4.0.4
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setQuery() {
        communicator.query.asLiveData().observe(viewLifecycleOwner) {
            viewModel.query.value = it
        }
    }


    private fun checkNoResult() {
        binding.apply {
            tvLabel.text = getString(R.string.no_result)
            intro.isVisible =
                (!eventView.isVisible && !holidayView.isVisible && !noticeView.isVisible && !syllabusView.isVisible)
        }

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setEventView() {
        val eventAdapter = EventAdapter { event ->
            onEventClick(event)
        }
        binding.apply {
            showSearchEvent.apply {
                adapter = eventAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.event.observe(viewLifecycleOwner) {
            binding.eventView.isVisible = it.isNotEmpty() && searchPreference.event
            eventAdapter.submitList(it)
            if (isEmpty) checkNoResult()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setHolidayView() {
        val holidayAdapter = HolidayHomeAdapter()
        binding.apply {
            showSearchHoliday.apply {
                adapter = holidayAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.holiday.observe(viewLifecycleOwner) {
            binding.holidayView.isVisible =
                it.isNotEmpty() && searchPreference.holiday
            holidayAdapter.submitList(it)
            if (isEmpty) checkNoResult()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setNoticeView() {
        val noticeAdapter = Notice3Adapter(db, { notice, view ->
            navigationToNotice3Description(notice, view)
        }, { link ->
            navigateToImageView(link)
        })
        binding.apply {
            showSearchNotice.apply {
                adapter = noticeAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.notice.observe(viewLifecycleOwner) {
            binding.noticeView.isVisible = it.isNotEmpty() && searchPreference.notice
            noticeAdapter.submitList(it)
            if (isEmpty) checkNoResult()
        }
    }

    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        val action = NavGraphDirections.actionGlobalViewImageFragment(link)
        findNavController().navigate(action)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setSubjectView() {
        val syllabusAdapter = SyllabusHomeAdapter(listener = { s, y ->
            setOnSyllabusClickListener(s, y)
        }, request = REQUEST_ADAPTER_SEARCH)
        binding.apply {
            showSearchSubjects.apply {
                adapter = syllabusAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.subject.observe(viewLifecycleOwner) {
            binding.syllabusView.isVisible =
                it.isNotEmpty() && searchPreference.subject
            syllabusAdapter.submitList(it)
            if (isEmpty) checkNoResult()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    //    Syllabus
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


    //    Holiday Click
    private fun setOnHomeHolidayClickListener() {
        try {
            val action = SearchFragmentDirections.actionSearchFragmentToHolidayFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Click one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //    Event
    private fun onEventClick(event: Event) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        val action = NavGraphDirections.actionGlobalEventDescriptionFragment(
            path = event.path,
            title = event.society
        )
        findNavController().navigate(action)
    }


    override fun onPause() {
        super.onPause()
        communicator.openFirst = false
        CourseFragment.myScrollViewerInstanceState = binding.scrollViewSearch.onSaveInstanceState()
    }

    override fun onDestroy() {
        super.onDestroy()
        communicator.query.value = getString(R.string.blank)
        communicator.openFirst = true
    }

    companion object {
        var myScrollViewerInstanceState: Parcelable? = null
    }

}