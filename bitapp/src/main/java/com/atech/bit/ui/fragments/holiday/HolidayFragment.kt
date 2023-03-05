package com.atech.bit.ui.fragments.holiday

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHolidayBinding
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.utils.sortBySno
import com.atech.core.utils.CURRENT_YEAR
import com.atech.core.utils.DataState
import com.atech.core.utils.RemoteConfigUtil
import com.atech.core.utils.TAG
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.showSnackBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HolidayFragment : Fragment(R.layout.fragment_holiday) {

    private val binding: FragmentHolidayBinding by viewBinding()
    val viewModel: HolidayViewModel by viewModels()


    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var remoteConfigUtil: RemoteConfigUtil

    private lateinit var holidayAdapter: HolidayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        setTitle(toolbar)

        holidayAdapter = HolidayAdapter()
        binding.apply {
            showHoliday.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                    }
                )
                adapter = holidayAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
        viewModel.query.value = "main"


        binding.apply {
            toggleChip.check(R.id.bt_main)

            btRes.setOnClickListener {
                viewModel.query.value = "res"
            }
            btMain.setOnClickListener {
                viewModel.query.value = "main"
            }
        }
        detectScroll()

        getData()
    }

    private fun getData() {
        viewModel.getHoliday().observe(viewLifecycleOwner) {
            when (it) {
                DataState.Empty -> {
                    binding.progressBarHoliday.isVisible = true
                }

                is DataState.Error -> binding.root.showSnackBar(
                    "${it.exception.message}",
                    Snackbar.LENGTH_SHORT
                )

                DataState.Loading -> {

                }

                is DataState.Success -> {
                    Log.d(TAG, "getData:${it.data.holidays} ")
                    binding.progressBarHoliday.isVisible = false
                    holidayAdapter.submitList(it.data.holidays.sortBySno())
                }
            }
        }
    }




    private fun setTitle(toolbar: Toolbar?) {
        remoteConfigUtil.fetchData({
            toolbar?.title = "Holiday"
        }
        ) {
            val year = remoteConfigUtil.getString(CURRENT_YEAR)
            toolbar?.title = resources.getString(R.string.holiday_title, year)
        }
    }

    private fun detectScroll() {
        binding.nestedViewHoliday.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            when (scrollY) {
                0 -> {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        com.google.android.material.R.attr.colorSurface
                    )
                }

                else -> {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        R.attr.bottomBar
                    )
                }
            }
        }
    }
}