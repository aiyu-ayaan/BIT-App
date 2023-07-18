package com.atech.bit.ui.fragments.cgpa

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowCgpaBinding
import com.atech.theme.checkCgpa
import com.atech.theme.checkEarnCredits
import com.atech.theme.combineTextChangeListener

class CgpaAdapter : RecyclerView.Adapter<CgpaAdapter.CgpaViewHolder>(), CgpaValueListener {


    private val semesterValues = mutableMapOf<Int, Pair<String, String>>()
    var list = listOf<Pair<Double, Double?>>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CgpaViewHolder(
        private val binding: RowCgpaBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        var listener = this@CgpaAdapter

        init {
            binding.outlinedTextSem.combineTextChangeListener(
                binding.outlinedTextEarnCredits
            ) { (cgpa, earnCredits) ->
                if (cgpa.checkCgpa()) {
                    semesterValues[absoluteAdapterPosition] = Pair("0", earnCredits)
                    return@combineTextChangeListener
                }
                if (earnCredits.checkEarnCredits()) {
                    semesterValues[absoluteAdapterPosition] = Pair(cgpa, "0")
                    return@combineTextChangeListener
                }
                listener.onCgpaValueEntered(absoluteAdapterPosition, Pair(cgpa, earnCredits))
            }
        }

        fun bind(gradeWithCr: Pair<Double, Double?>) {
            binding.apply {
                outlinedTextSem.apply {
                    hint = "Semester ${absoluteAdapterPosition + 1}"
                    editText?.setText(
                        if (gradeWithCr.first == 0.0)
                            ""
                        else
                            gradeWithCr.first.toString()
                    )
                }
                outlinedTextEarnCredits.editText?.setText(
                    if ((gradeWithCr.second ?: 0.0) == 0.0)
                        ""
                    else
                        gradeWithCr.second.toString()
                )
            }
        }

        fun getValue() = binding.outlinedTextSem.editText?.text.toString()
            .toDouble() to binding.outlinedTextEarnCredits.editText?.text.toString().toDouble()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CgpaViewHolder =
        CgpaViewHolder(
            RowCgpaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ).also {
            it.listener = this
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CgpaViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onCgpaValueEntered(position: Int, value: Pair<String, String>) {
        semesterValues[position] = value
    }


    fun getAllSemesterValues(): List<Pair<String, String>> {
        return semesterValues.values.toList()
    }
}

interface CgpaValueListener {
    fun onCgpaValueEntered(position: Int, value: Pair<String, String>)
}

