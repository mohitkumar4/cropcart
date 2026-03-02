package com.example.cropcart.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cropcart.R

class CartItemAdapter(
    private var cartList: MutableList<CartItem>,
    // UI -> Activity callbacks:
    private val onQuantityChanged: (item: CartItem, newQuantity: Int) -> Unit,
    private val onItemRemoved: (item: CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.cartItemImage)
        val name: TextView = itemView.findViewById(R.id.cartItemName)
        val price: TextView = itemView.findViewById(R.id.cartItemPrice)
        val quantityText: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val btnIncrease: TextView = itemView.findViewById(R.id.btnIncreaseQuantity)
        val btnDecrease: TextView = itemView.findViewById(R.id.btnDecreaseQuantity)
        val btnDelete: Button? = itemView.findViewById(R.id.btnRemoveItem) // optional
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        Glide.with(holder.itemView.context)
            .load(item.image)
            .placeholder(R.drawable.img)
            .into(holder.image)

        holder.name.text = item.name
        holder.price.text = "₹${item.price * item.quantity}"
        holder.quantityText.text = item.quantity.toString()

        holder.btnIncrease.setOnClickListener {
            val newQ = item.quantity + 1
            // update local UI immediately
            item.quantity = newQ
            holder.quantityText.text = newQ.toString()
            holder.price.text = "₹${item.price * newQ}"
            // inform activity to persist change
            onQuantityChanged(item, newQ)
        }

        holder.btnDecrease.setOnClickListener {
            val newQ = item.quantity - 1
            if (newQ > 0) {
                item.quantity = newQ
                holder.quantityText.text = newQ.toString()
                holder.price.text = "₹${item.price * newQ}"
                onQuantityChanged(item, newQ)
            } else {
                // item should be removed
                onItemRemoved(item)
            }
        }

        holder.btnDelete?.setOnClickListener {
            onItemRemoved(item)
        }
    }

    override fun getItemCount(): Int = cartList.size

    fun updateCart(newCartList: List<CartItem>) {
        cartList.clear()
        cartList.addAll(newCartList)
        notifyDataSetChanged()
    }
}
