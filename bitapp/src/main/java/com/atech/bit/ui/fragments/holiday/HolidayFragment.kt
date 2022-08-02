package com.atech.bit.ui.fragments.holiday

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.databinding.FragmentHolidayBinding
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.showSnackBar
import com.atech.core.utils.DataState
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        setTitle(toolbar)

        val holidayAdapter = HolidayAdapter()
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.holidays.collect { main ->
                when (main) {
                    DataState.Loading -> {
                        binding.progressBarHoliday.isVisible = true
                    }

                    is DataState.Success -> {
                        binding.progressBarHoliday.isVisible = false
                        holidayAdapter.submitList(main.data)
                    }
                    DataState.Empty -> {

                    }
                    is DataState.Error -> {

                        binding.root.showSnackBar(
                            "${main.exception.message}",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                }
            }
        }
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

    }

    private fun setTitle(toolbar: Toolbar?) {
        db.collection("Utils")
            .document("holiday_title")
            .addSnapshotListener { value, _ ->
                val title = value?.get("Title")
                toolbar?.title = (title ?: "Holiday").toString()
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