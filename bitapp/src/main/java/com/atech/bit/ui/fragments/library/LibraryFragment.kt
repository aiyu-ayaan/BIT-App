package com.atech.bit.ui.fragments.library

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentLibraryBinding
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val binding: FragmentLibraryBinding by viewBinding()
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var libraryAdapter: LibraryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        libraryAdapter = LibraryAdapter()
        binding.apply {
            setRecyclerView()
            buttonClick()
        }
        getData()
    }

    private fun getData() = lifecycleScope.launchWhenStarted {
        viewModel.libraryList.collect {
            libraryAdapter.submitList(it)
        }
    }

    private fun FragmentLibraryBinding.buttonClick() {
        val transitionName = resources.getString(R.string.add_books)
        fabAddBook.transitionName = transitionName
        fabAddBook.setOnClickListener {
            navigateToAddEdit(
                transitionName,
            )
        }
    }

    private fun FragmentLibraryBinding.navigateToAddEdit(string: String) {
        val extras =
            FragmentNavigatorExtras(fabAddBook to string)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = LibraryFragmentDirections.actionLibraryFragmentToAddEditFragment(
            string
        )
        findNavController().navigate(action, extras)
    }

    private fun FragmentLibraryBinding.setRecyclerView() =
        this.recyclerViewLibrary.apply {
            adapter = libraryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
}