package com.example.cropcart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyProductsAdapter(
    private var productList: List<FeaturedProduct>,
    private val onEditClicked: (FeaturedProduct) -> Unit,
    private val onDeleteClicked: (FeaturedProduct) -> Unit
) : RecyclerView.Adapter<MyProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.productImage)
        val name: TextView = view.findViewById(R.id.productName)
        val price: TextView = view.findViewById(R.id.productPrice)
        val editBtn: TextView = view.findViewById(R.id.btnEdit)
        val deleteBtn: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = "₹${product.price} / kg"

        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.img)
            .into(holder.image)

        holder.editBtn.setOnClickListener { onEditClicked(product) }
        holder.deleteBtn.setOnClickListener { onDeleteClicked(product) }
    }

    override fun getItemCount() = productList.size

    fun updateList(newList: List<FeaturedProduct>) {
        productList = newList
        notifyDataSetChanged()
    }
}