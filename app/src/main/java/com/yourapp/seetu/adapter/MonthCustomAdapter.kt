package com.yourapp.seetu.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.yourapp.seetu.databinding.MonthCardViewBinding
import com.yourapp.seetu.model.MonthModel

class MonthCustomAdapter(
    private val context : Context?,
    private val monthList : List<MonthModel>
) : RecyclerView.Adapter<MonthCustomAdapter.MyViewHolder>() {

    private lateinit var index0 : String
    private lateinit var index1 : String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = MonthCardViewBinding.inflate(view, parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int ) {

        holder.yelamTextView.text = "₹"+monthList[position].yelam.toString()
        holder.amountTextView.text = "₹"+monthList[position].toPay.toString()
        holder.pendingTextView.text = "₹"+monthList[position].pending.toString()

        getIndex(monthList[position].month!!)

        if(index0 != ""){
            val uri : String = "@drawable/number$index0"
            val imgRes = context?.resources?.getIdentifier(uri, null, context.packageName)
            val imgDrawable = context?.getDrawable(imgRes!!)

            //val imgDrawable = AppCompatResources.getDrawable(context ,imgRes)
            holder.monthNumberIndex0ImageView.setImageDrawable(imgDrawable)
        }

        if(index1 != ""){
            val uri : String = "@drawable/number$index1"
            val imgRes = context?.resources?.getIdentifier(uri, null, context.packageName)
            val imgDrawable = context?.getDrawable(imgRes!!)
            //val imgDrawable = AppCompatResources.getDrawable(context ,imgRes)
            holder.monthNumberIndex1ImageView.setImageDrawable(imgDrawable)
        }
    }

    override fun getItemCount() = monthList.size

    private fun getIndex(position: Int){
        if(position < 100){
            if(position < 10){
                index0 = (position%10).toString()
                index1 =""
            }
            else{
                index0 = (position/10).toString()
                index1 = (position%10).toString()
            }
        }
        else {
            index0 = ""
            index1 = ""
        }
    }

    inner class MyViewHolder(private val binding : MonthCardViewBinding):
        RecyclerView.ViewHolder(binding.root){
        val yelamTextView : TextView = binding.monthCardYelam
        val amountTextView : TextView = binding.monthCardToPay
        val pendingTextView : TextView = binding.monthCardPending
        val monthNumberIndex0ImageView : ImageView = binding.monthCardNumberIndex0
        val monthNumberIndex1ImageView : ImageView = binding.monthCardNumberIndex1

    }
}
