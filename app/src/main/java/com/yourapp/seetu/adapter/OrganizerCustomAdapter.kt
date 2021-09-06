package com.yourapp.seetu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yourapp.seetu.databinding.OrganizerCardViewBinding
import com.yourapp.seetu.model.OrganizerInfoModel

class OrganizerCustomAdapter(
    private val organizersList : List<OrganizerInfoModel>,
    private val onItemClicked : (position : Int) -> Unit
) : RecyclerView.Adapter<OrganizerCustomAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerCustomAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrganizerCardViewBinding.inflate(view, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTextView.text = organizersList[position].name
        holder.localityTextView.text = organizersList[position].area
    }

    override fun getItemCount() = organizersList.size

    inner class MyViewHolder(private val binding : OrganizerCardViewBinding):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val nameTextView = binding.organizerCardName
        val localityTextView = binding.organizerCardLocality

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onItemClicked(absoluteAdapterPosition)
        }
    }
}