package com.atech.bit.ui.fragments.society

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentSocietyBinding
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.core.api.society.Society
import com.atech.core.utils.DataState
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.onScrollColorChange
import com.atech.core.utils.showSnackBar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocietyFragment : Fragment(R.layout.fragment_society) {

    private val binding: FragmentSocietyBinding by viewBinding()
    private val viewModel: SocietyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        val societyAdapter = SocietyAdapter { society, _ ->
            setOnSocietyClickListener(society)
        }
        val ngosAdapter = SocietyAdapter { society, _ ->
            setOnSocietyClickListener(society)
        }
        binding.apply {
            showSociety.apply {
                adapter = societyAdapter
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                    }
                )
            }
            showNgos.apply {
                adapter = ngosAdapter
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
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
        societyAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        viewModel.getSociety().observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    binding.materialCardViewMain.isVisible = dataState.data.societies.isNotEmpty()
                    binding.textViewSociety.isVisible = dataState.data.societies.isNotEmpty()
                    binding.materialCardViewNgo.isVisible = dataState.data.ngos.isNotEmpty()
                    binding.textViewNgos.isVisible = dataState.data.ngos.isNotEmpty()
                    societyAdapter.submitList(dataState.data.societies.sortBySno())
                    ngosAdapter.submitList(dataState.data.ngos.sortBySno())
                }

                is DataState.Error -> {
                    binding.materialCardViewNgo.isVisible = false
                    binding.textViewNgos.isVisible = false
                    binding.materialCardViewMain.isVisible = false
                    binding.textViewSociety.isVisible = false
                    binding.root.showSnackBar(
                        dataState.exception.message.toString(),
                        -1
                    )
                }

                else -> {}
            }
        }
        detectScroll()
    }


    private fun setOnSocietyClickListener(society: Society) {
        try {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            val action =
                SocietyFragmentDirections.actionSocietyFragmentToEventSocietyDescriptionFragment(
                    society = society,
                    title = society.name
                )
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Click one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        activity?.onScrollColorChange(binding.nestedScrollViewSociety, {
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

    

    /**
    * @auther Nilay
    * @since 4.1.1 Patch 3
    */
    private fun List<Society>?.sortBySno(): List<Society> {
        return this?.sortedBy { it.sno } ?: emptyList()
    }
}