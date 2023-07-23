package com.atech.bit.ui.fragments.holiday

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHolidayBinding
import com.atech.bit.ui.fragments.holiday.adapter.HolidayAdapter
import com.atech.core.retrofit.ApiCases
import com.atech.core.utils.DataState
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.launchWhenResumed
import com.atech.theme.set
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@AndroidEntryPoint
class HolidayFragment : BaseFragment(R.layout.fragment_holiday, Axis.Y) {
    private val binding: FragmentHolidayBinding by viewBinding()

    @Inject
    lateinit var cases: ApiCases

    private lateinit var holidayAdapter: HolidayAdapter

    private val query = MutableStateFlow("main")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setChip()
            setRecyclerView()
        }
        observeData()
    }

    private fun FragmentHolidayBinding.setChip() = this.apply {
        toggleChip.check(R.id.bt_main)
        btRes.setOnClickListener {
            query.value = "res"
        }
        btMain.setOnClickListener {
            query.value = "main"
        }
    }

    private fun FragmentHolidayBinding.setRecyclerView() = this.recyclerView.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = HolidayAdapter().also { holidayAdapter = it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() = launchWhenResumed {
        query.flatMapLatest {
            cases.holiday.invoke(it)
        }.collectLatest {
            when (it) {
                is DataState.Error -> {
                    toast(
                        it.exception.message
                            ?: getString(com.atech.theme.R.string.something_went_wrong),
                    )
                }

                is DataState.Success -> holidayAdapter.submitList(it.data.holidays)
                else -> Unit
            }
        }
    }

    private fun FragmentHolidayBinding.setToolbar() {
        includeToolbar.set(
            ToolbarData(
                title = com.atech.theme.R.string.holiday,
                action = { findNavController().navigateUp() }
            )
        )
    }
}