package com.atech.bit.ui.fragments.global_search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.LayoutTitleBinding
import com.atech.bit.databinding.RowHolidayHomeBinding
import com.atech.bit.databinding.RowNotice3Binding
import com.atech.bit.databinding.RowSyllabusHomeBinding
import com.atech.bit.ui.fragments.global_search.model.SearchItem

class SearchRecyclerViewAdapter(
    private val onClick : (view : View , item : SearchItem) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<SearchRecyclerViewHolder>() {


    var items = mutableListOf<SearchItem>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecyclerViewHolder =
        when (viewType) {
            R.layout.layout_title ->
                SearchRecyclerViewHolder.Title(
                    LayoutTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            R.layout.row_syllabus_home ->
                SearchRecyclerViewHolder.Syllabus(
                    RowSyllabusHomeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ){ view, _, position ->
                    onClick.invoke(view, this.items[position])
                }

            R.layout.row_holiday_home ->
                SearchRecyclerViewHolder.Holiday(
                    RowHolidayHomeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            EVENT_POST ->
                SearchRecyclerViewHolder.Event(
                    RowNotice3Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ){ view, _, position ->
                    onClick.invoke(view, this.items[position])
                }

            NOTICE_POST ->
                SearchRecyclerViewHolder.Notice(
                    RowNotice3Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ){ view, _, position ->
                    onClick.invoke(view, this.items[position])
                }

            else -> throw IllegalArgumentException("Invalid view type")
        }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SearchRecyclerViewHolder, position: Int) =
        when (holder) {
            is SearchRecyclerViewHolder.Title -> holder.bind(items[position] as SearchItem.Title)
            is SearchRecyclerViewHolder.Syllabus -> holder.bind(items[position] as SearchItem.Syllabus)
            is SearchRecyclerViewHolder.Holiday -> holder.bind(items[position] as SearchItem.Holiday)
            is SearchRecyclerViewHolder.Event -> holder.bind(items[position] as SearchItem.Event)
            is SearchRecyclerViewHolder.Notice -> holder.bind(items[position] as SearchItem.Notice)
        }


    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is SearchItem.Title -> R.layout.layout_title
        is SearchItem.Syllabus -> R.layout.row_syllabus_home
        is SearchItem.Holiday -> R.layout.row_holiday_home
        is SearchItem.Event -> EVENT_POST
        is SearchItem.Notice -> NOTICE_POST
    }


    companion object {
        const val EVENT_POST = 45 * 1000
        const val NOTICE_POST = 46 * 1000
    }
}