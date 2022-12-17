package com.atech.bit.ui.fragments.library

import android.animation.LayoutTransition
import android.transition.Fade
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowLibraryBinding
import com.atech.core.data.room.library.DiffUtilCallbackLibrary
import com.atech.core.data.room.library.LibraryModel
import com.google.android.material.button.MaterialButton

class LibraryAdapter(
    private val onDeleteClick: (LibraryModel) -> Unit = {},
    private val onMarkAsReturnClick: (LibraryModel) -> Unit = { },
    private val listener: (LibraryModel, View) -> Unit
) : ListAdapter<LibraryModel, LibraryAdapter.LibraryViewHolder>(DiffUtilCallbackLibrary()) {

    inner class LibraryViewHolder(
        private val binding: RowLibraryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {

            binding.buttonMarkAsReturned.setOnClickListener {
                absoluteAdapterPosition.let { position ->
                    if (position != RecyclerView.NO_POSITION) {
                        val library = getItem(position)
                        binding.divider.isVisible = false
                        binding.buttonDelete.visibility = View.GONE
                        binding.buttonMarkAsReturned.visibility = View.GONE
                        onMarkAsReturnClick(library)
                    }
                }
            }
            binding.buttonDelete.setOnClickListener {
                absoluteAdapterPosition.let { position ->
                    if (position != RecyclerView.NO_POSITION) {
                        val library = getItem(position)
                        onDeleteClick(library)
                    }
                }
            }

            binding.root.setOnClickListener {
                absoluteAdapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
//                        val isVisible = binding.divider.visibility == View.VISIBLE
//                        TransitionManager.beginDelayedTransition(
//                            binding.root, TransitionSet().addTransition(Fade())
//                        )
//                        binding.apply {
//                            root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//                            divider.visibility = if (isVisible) View.GONE else View.VISIBLE
//                            buttonDelete.visibility = if (isVisible) View.GONE else View.VISIBLE
//                            buttonMarkAsReturned.visibility =
//                                if (isVisible) View.GONE else View.VISIBLE
//                        }
                    }
                }
            }
            binding.floatingActionButton.setOnClickListener {
                absoluteAdapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        listener(getItem(it), binding.floatingActionButton)
                    }
                }
            }
        }

        fun bind(libraryModel: LibraryModel) {
            binding.apply {
                textViewBookId.text = libraryModel.bookId.ifBlank { "No Id" }
                textViewIssueBookName.text = libraryModel.bookName
                textViewIssueBookName.setCompoundDrawablesWithIntrinsicBounds(
                    if (libraryModel.markAsReturn) R.drawable.ic_check_circle else 0, 0, 0, 0
                )
//                buttonMarkAsReturned set icon image button
                (buttonMarkAsReturned as MaterialButton).setIconResource(
                    if (libraryModel.markAsReturn) R.drawable.ic_close else R.drawable.ic_check
                )

                textViewIssueDate.text =
                    String.format("Issued on : %s", libraryModel.issueFormatData)
                floatingActionButton.transitionName = libraryModel.bookName
                textViewReturnDate.text = libraryModel.returnFormatData
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder =
        LibraryViewHolder(
            RowLibraryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}