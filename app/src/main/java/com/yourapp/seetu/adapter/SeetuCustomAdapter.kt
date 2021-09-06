package com.yourapp.seetu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yourapp.seetu.R
import com.yourapp.seetu.databinding.SeetuCardViewBinding

class SeetuCustomAdapter(
    private val context : Context?,
    private val seetuNameList : List<String>,
    private val seetuPendingList : List<String>,
    private val seetuAmountList : List<String>,
    private val seetuMonthsList : List<String>,
    private val onItemClicked : (position : Int) -> Unit
) : RecyclerView.Adapter<SeetuCustomAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = SeetuCardViewBinding.inflate(view, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.seetuNameText.text = seetuNameList[position]
        holder.seetuPendingText.text = "₹" + seetuPendingList[position]
        holder.seetuAmountText.text = "₹" + seetuAmountList[position]
        val monthsString = context?.getString(R.string.months)
        holder.seetuMonthsText.text = seetuMonthsList[position] + " " +monthsString
    }

    override fun getItemCount() = seetuNameList.size

    inner class MyViewHolder(private val binding : SeetuCardViewBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val seetuNameText = binding.seetuCardName
        val seetuPendingText = binding.seetuCardPending
        val seetuAmountText = binding.seetuCardAmount
        val seetuMonthsText = binding.seetuCardMonths

        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            onItemClicked(absoluteAdapterPosition)
        }
    }


}