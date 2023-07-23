package com.atech.bit.ui.fragments.library

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentLibraryBinding
import com.atech.core.room.library.LibraryModel
import com.atech.core.utils.CalendarReminder
import com.atech.theme.Axis
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.exitTransition
import com.atech.theme.navigate
import com.atech.theme.set
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment(R.layout.fragment_library,Axis.Y) {

    private val binding: FragmentLibraryBinding by viewBinding()
    private val viewModel: LibraryViewModel by activityViewModels()
    private lateinit var libraryAdapter: LibraryAdapter
    private var list: List<LibraryModel> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setRecyclerView()
            appBar()
            fabClick()
        }
        observeData()
    }

    private fun observeData() {
        viewModel.libraryList.observe(viewLifecycleOwner) {
            binding.emptyAnimation.isVisible = it.isEmpty()
            libraryAdapter.submitList(it)
            list = it
            val layoutManager = binding.rvShowBooks.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(0, 0)
        }
    }

    private fun FragmentLibraryBinding.setRecyclerView() = this.rvShowBooks.apply {
        adapter = LibraryAdapter(
            onMarkAsReturnClick = ::markAsReturn,
            onDeleteClick = ::deleteBook,
            listener = ::onEditClick
        ).also { libraryAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onEditClick(model: LibraryModel) {
        navigateToAddEdit(
            model.bookName,
            model
        )
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


    private fun FragmentLibraryBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.library, action = findNavController()::navigateUp
            )
        )
    }

    private fun FragmentLibraryBinding.fabClick() = this.fabAdd.apply {
        setOnClickListener {
            navigateToAddEdit(
                resources.getString(com.atech.theme.R.string.add_books)
            )
        }
    }


    private fun navigateToAddEdit(
        string: String, libraryModel: LibraryModel? = null
    ) {
        exitTransition(Axis.X)
        val action = LibraryFragmentDirections.actionLibraryFragmentToAddEditFragment(
            string, libraryModel
        )
        navigate(action)
    }

    //    ------------------------------------ Input output ------------------------------------
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

    private fun deleteBook(it: LibraryModel) {
        if (it.eventId != -1L) {
            CalendarReminder.deleteEvent(requireContext(), it.eventId)
        }
        viewModel.deleteBook(it)
    }
}