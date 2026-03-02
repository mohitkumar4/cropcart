package com.example.cropcart.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropcart.R

class SimilarAdapter(
    private var productList: List<FeaturedProduct>,
    private val onItemClick: (FeaturedProduct) -> Unit
) : RecyclerView.Adapter<SimilarAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.featuredImage)
        val name: TextView = itemView.findViewById(R.id.featuredName)
        val price: TextView = itemView.findViewById(R.id.featuredPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.similar_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = "₹${product.price}"
        Glide.with(holder.itemView.context)
            .load(product.image)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount() = productList.size

    fun updateList(newList: List<FeaturedProduct>) {
        productList = newList
        notifyDataSetChanged()
    }

}