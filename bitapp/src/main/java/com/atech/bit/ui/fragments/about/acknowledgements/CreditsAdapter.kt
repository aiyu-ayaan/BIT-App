package com.atech.bit.ui.fragments.about.acknowledgements

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowCreditBinding

class CreditsAdapter(
    private val onClick: (Credit) -> Unit = {}
) : RecyclerView.Adapter<CreditsAdapter.CreditViewHolder>() {
    var list = listOf<Credit>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CreditViewHolder(
        private val binding: RowCreditBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick.invoke(list[position])
                }
            }
        }

        fun bind(credit: Credit) {
            binding.apply {
                textViewName.text = credit.title
                textViewLicense.text = credit.licenses.joinToString { it.title }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditViewHolder =
        CreditViewHolder(
            RowCreditBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: CreditViewHolder, position: Int) {
        holder.bind(list[position])
    }

}