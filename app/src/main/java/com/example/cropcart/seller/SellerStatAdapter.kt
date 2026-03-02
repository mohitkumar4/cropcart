package com.example.cropcart.seller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R

data class SellerStat(val title: String, val value: String, val iconRes: Int)

class SellerStatAdapter(private val stats: List<SellerStat>) :
    RecyclerView.Adapter<SellerStatAdapter.StatViewHolder>() {

    inner class StatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iconStat)
        val title: TextView = itemView.findViewById(R.id.titleStat)
        val value: TextView = itemView.findViewById(R.id.valueStat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seller_stat, parent, false)
        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        val stat = stats[position]
        holder.icon.setImageResource(stat.iconRes)
        holder.title.text = stat.title
        holder.value.text = stat.value
    }

    override fun getItemCount() = stats.size
}
