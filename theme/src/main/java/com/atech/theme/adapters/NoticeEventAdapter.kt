package com.atech.theme.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.theme.databinding.RowNoticeEventBinding

class NoticeEventAdapter<T>(
    private val apply: RowNoticeEventBinding.(data: T) -> Unit = {},
) : RecyclerView.Adapter<NoticeEventAdapter.NoticeEventViewHolder<T>>() {

    var items = mutableListOf<T>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class NoticeEventViewHolder<T>(
        private val binding: RowNoticeEventBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(data: T, apply: RowNoticeEventBinding.(T) -> Unit) {
            binding.apply {
                apply.invoke(this, data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeEventViewHolder<T> =
        NoticeEventViewHolder(
            RowNoticeEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NoticeEventViewHolder<T>, position: Int) {
        holder.bind(items[position], apply)
    }


}