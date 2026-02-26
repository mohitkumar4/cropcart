package com.example.cropcart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//data class FeaturedProduct(
//    val name: String,
//    val price: String,
//    val description: String,
//    val image: String
//)

class FeaturedProductAdapter(
    private val onItemClick: (FeaturedProduct) -> Unit = {}
) : ListAdapter<FeaturedProduct, FeaturedProductAdapter.FeaturedViewHolder>(DiffCallback()) {

    inner class FeaturedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.featuredImage)
        private val name: TextView = itemView.findViewById(R.id.featuredName)
        private val price: TextView = itemView.findViewById(R.id.featuredPrice)
        private val description: TextView = itemView.findViewById(R.id.featuredDescription)

        fun bind(item: FeaturedProduct) {
            name.text = item.name
            price.text = item.price.toString()

            Glide.with(itemView.context)
                .load(item.image)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(image)

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.featured_item, parent, false)
        return FeaturedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeaturedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FeaturedProduct>() {
        override fun areItemsTheSame(oldItem: FeaturedProduct, newItem: FeaturedProduct): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: FeaturedProduct, newItem: FeaturedProduct): Boolean {
            return oldItem == newItem
        }
    }
}
