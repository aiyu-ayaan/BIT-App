package com.atech.bit.ui.fragments.library

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentLibraryBinding
import com.atech.core.data.room.library.LibraryModel
import com.atech.core.utils.CalendarReminder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val binding: FragmentLibraryBinding by viewBinding()
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var libraryAdapter: LibraryAdapter
    private var list: List<LibraryModel> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        libraryAdapter = LibraryAdapter(
            onDeleteClick = {
                deleteBook(it)
            },
            onMarkAsReturnClick = { lab ->
                markAsReturn(lab)
            },
        ) { l, fab ->
            navigateToAddEdit(l.bookName, fab, l)
        }
        binding.apply {
            setRecyclerView()
            buttonClick()
            appBar()
        }
        getData()
    }

    private fun FragmentLibraryBinding.appBar() {
        bottomAppBar.apply {
            setNavigationOnClickListener {
                // show dialog for delete all books
                MaterialAlertDialogBuilder(requireContext()).setTitle("Delete All Books")
                    .setMessage("Are you sure you want to delete all books?")
                    .setPositiveButton("Yes") { _, _ ->
                        list.forEach {
                            deleteBook(it)
                        }
                    }.setNegativeButton("No", null).show()
            }
        }
    }

    private fun deleteBook(it: LibraryModel) {
        if (it.eventId != -1L) {
            CalendarReminder.deleteEvent(requireContext(), it.eventId)
        }
        viewModel.deleteBook(it)
    }

    private fun markAsReturn(it: LibraryModel) {
        val markAsReturn = it.markAsReturn
        if (it.eventId != -1L) {
            CalendarReminder.deleteEvent(requireContext(), it.eventId)
        }
        viewModel.updateBook(
            it.copy(
                eventId = -1L, alertDate = 0L, markAsReturn = !markAsReturn
            )
        )
    }

    private fun getData() {
        viewModel.libraryList.asLiveData().observe(viewLifecycleOwner) {
            binding.emptyAnimation.isVisible = it.isEmpty()
            libraryAdapter.submitList(it)
            list = it
            val layoutManager = binding.recyclerViewLibrary.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(0, 0)
        }
    }


    private fun FragmentLibraryBinding.buttonClick() {
        val transitionName = resources.getString(R.string.add_books)
        fabAddBook.transitionName = transitionName
        fabAddBook.setOnClickListener {
            navigateToAddEdit(
                transitionName, it
            )
        }
    }

    private fun navigateToAddEdit(
        string: String, fabAddBook: View, libraryModel: LibraryModel? = null
    ) {
        val extras = FragmentNavigatorExtras(fabAddBook to string)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = LibraryFragmentDirections.actionLibraryFragmentToAddEditFragment(
            string, libraryModel
        )
        findNavController().navigate(action, extras)
    }

    private fun FragmentLibraryBinding.setRecyclerView() = this.recyclerViewLibrary.apply {
        adapter = libraryAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}