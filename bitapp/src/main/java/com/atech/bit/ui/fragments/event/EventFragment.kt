package com.atech.bit.ui.fragments.event

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentEventBinding
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.utils.MainStateEvent
import com.atech.core.data.ui.event.Event
import com.atech.core.utils.*
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventFragment : Fragment(R.layout.fragment_event) {

    private val binding: FragmentEventBinding by viewBinding()
    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreColor()
        val eventAdapter = EventAdapter { event ->
            onEventClick(event)
        }
        binding.apply {
            showEvent.apply {
                adapter = eventAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                    }
                )
            }
        }
        eventAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        viewModel.setStateListenerMain(MainStateEvent.GetData)
        viewModel.dataStateMain.observe(viewLifecycleOwner) { dateState ->
            when (dateState) {
                is DataState.Success -> {
                    binding.empty.visibility = View.GONE
                    eventAdapter.submitList(dateState.data)
                }
                DataState.Empty -> {
                    binding.empty.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    binding.empty.visibility = View.VISIBLE
                    binding.root.showSnackBar(
                        "${dateState.exception.message}", -1
                    )
                }
                DataState.Loading -> {

                }
            }
        }
        detectScroll()

    }

    private fun onEventClick(event: Event) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        val action = NavGraphDirections.actionGlobalEventDescriptionFragment(
            path = event.path,
            title = event.society
        )
        findNavController().navigate(action)
    }

    private fun detectScroll() {
        activity?.onScrollColorChange(binding.nestedScrollViewEvent, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                com.google.android.material.R.attr.colorSurface
            )
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                R.attr.bottomBar
            )
        })
    }
    private fun restoreColor() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isColored.collect {
                if (it) {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        R.attr.bottomBar
                    )
                } else {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        com.google.android.material.R.attr.colorSurface
                    )
                }
            }
        }
    }

     
}