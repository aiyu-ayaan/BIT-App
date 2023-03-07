package com.atech.bit.ui.fragments.event

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentEventBinding
import com.atech.bit.utils.launchWhenStarted
import com.atech.core.data.ui.events.Events
import com.atech.core.utils.DataState
import com.atech.core.utils.MainStateEvent
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.onScrollColorChange
import com.atech.core.utils.showSnackBar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EventFragment : Fragment(R.layout.fragment_event) {

    private val binding: FragmentEventBinding by viewBinding()
    private val viewModel: EventViewModel by viewModels()

    @Inject
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        restoreColor()
        val eventAdapter = EventsAdapter(db, {
            navigateToImageView(it)
        }) { event, rootView ->
            navigateToEventDetail(event, rootView)
        }
        binding.apply {
            showEvent.apply {
                adapter = eventAdapter
                layoutManager = LinearLayoutManager(requireContext())
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

    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        val action = NavGraphDirections.actionGlobalViewImageFragment(link)
        findNavController().navigate(action)
    }

    private fun restoreColor() {
        launchWhenStarted {
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