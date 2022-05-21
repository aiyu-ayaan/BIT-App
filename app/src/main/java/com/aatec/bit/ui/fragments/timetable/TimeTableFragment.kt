package com.aatec.bit.ui.fragments.timetable

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentTimeTableBinding
import com.aatec.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.aatec.core.utils.DataState
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimeTableFragment : Fragment(R.layout.fragment_time_table) {

    private val binding: FragmentTimeTableBinding by viewBinding()
    private val viewModel: TimeTableViewModel by viewModels()
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val timeTableAdapter = TimeTableAdapter { link, title ->
//                navigateToViewImage(link, title)
            }
            binding.apply {
                recyclerViewShowAll.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = timeTableAdapter
                }
            }
            lifecycleScope.launchWhenStarted {
                viewModel.timeTable.collect { dateState ->
                    when (dateState) {
                        DataState.Empty -> {
                            Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
                        }
                        is DataState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "${dateState.exception}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        DataState.Loading -> {}
                        is DataState.Success -> timeTableAdapter.submitList(dateState.data)
                    }
                }
            }

//            setUpDefault()
//            detectScroll()
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}