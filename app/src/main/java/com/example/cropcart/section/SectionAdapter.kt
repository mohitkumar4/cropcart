package com.example.cropcart.section

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.product.FeaturedProductAdapter
import com.example.cropcart.product.ProductDetailActivity


class SectionAdapter(
    private var sectionList: List<Section>
) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    // Shared view pool for child RecyclerViews to improve performance
    private val viewPool = RecyclerView.RecycledViewPool()

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sectionTitle: TextView = itemView.findViewById(R.id.sectionTitle)
        val productRecycler: RecyclerView = itemView.findViewById(R.id.productRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sectionList[position]
        holder.sectionTitle.text = section.title

        // Configure child RecyclerView only once
        val layoutManager = LinearLayoutManager(
            holder.productRecycler.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.productRecycler.layoutManager = layoutManager
        holder.productRecycler.setRecycledViewPool(viewPool)

        val adapter = FeaturedProductAdapter { product ->
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("productId", product.id)
                putExtra("productCategory", product.category)
                putExtra("productSection", product.section)
                putExtra("productName", product.name)
                putExtra("productPrice", "₹ ${product.price}")
                putExtra(
                    "productDescription",
                    "High-quality ${product.name} directly from the farm."
                )
                putExtra("sellerName", "Farmer’s Market") // Replace with real seller if available
                putExtra("productImage", product.image)
            }
            context.startActivity(intent)
        }
        holder.productRecycler.adapter = adapter
        adapter.submitList(section.items)
    }

    override fun getItemCount() = sectionList.size

    fun submitList(newList: List<Section>) {
        sectionList = newList
        notifyDataSetChanged()
    }
}
