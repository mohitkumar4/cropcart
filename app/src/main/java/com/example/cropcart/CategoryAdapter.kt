package com.example.cropcart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category

        // Highlight selected
        holder.categoryName.isSelected = position == selectedPosition

        holder.itemView.setOnClickListener {
            val currentPosition = holder.absoluteAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val previousSelected = selectedPosition
                selectedPosition = currentPosition

                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)

                onCategoryClick(categories[currentPosition])
            }
        }
    }

    override fun getItemCount(): Int = categories.size
}
