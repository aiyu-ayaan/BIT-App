package com.atech.bit.ui.fragments.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowLibraryBinding
import com.atech.core.data.room.library.DiffUtilCallbackLibrary
import com.atech.core.data.room.library.LibraryModel

class LibraryAdapter() :
    ListAdapter<LibraryModel, LibraryAdapter.LibraryViewHolder>(DiffUtilCallbackLibrary()) {

    inner class LibraryViewHolder(
        private val binding: RowLibraryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                absoluteAdapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {

                    }
                }
            }
        }

        fun bind(libraryModel: LibraryModel) {
            binding.apply {
                textViewBookId.text =
                    libraryModel.bookId.ifBlank { "No Id" }
                textViewIssueBookName.text = libraryModel.bookName
                textViewIssueDate.text =
                    String.format("Issued on : %s", libraryModel.issueFormatData)
                textViewReturnDate.text = libraryModel.returnFormatData
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder =
        LibraryViewHolder(
            RowLibraryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}