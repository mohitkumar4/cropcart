package com.example.cropcart.seller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.orders.Order
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class SellerOrdersAdapter(
    private var orders: List<Order>
) : RecyclerView.Adapter<SellerOrdersAdapter.OrderViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderIdText)
        val totalPrice: TextView = itemView.findViewById(R.id.orderTotalPrice)
        val status: TextView = itemView.findViewById(R.id.orderStatusText)
        val btnShipped: Button = itemView.findViewById(R.id.btnMarkShipped)
        val btnDelivered: Button = itemView.findViewById(R.id.btnMarkDelivered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seller_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.orderId.text = "Order ID: ${order.id}"
        holder.totalPrice.text = "Total: ₹${order.totalPrice}"
        holder.status.text = "Status: ${order.status}"

        // Disable/Enable buttons based on status
        holder.btnShipped.isEnabled = order.status == "Pending"
        holder.btnDelivered.isEnabled = order.status == "Shipped"

        // Shipped button with confirmation
        holder.btnShipped.setOnClickListener {
            showConfirmationDialog(
                holder,
                order,
                "Mark as Shipped",
                "Are you sure you want to mark this order as 'Shipped'?",
                "Shipped"
            )
        }

        // Delivered button with confirmation
        holder.btnDelivered.setOnClickListener {
            showConfirmationDialog(
                holder,
                order,
                "Mark as Delivered",
                "Confirm this order has been delivered?",
                "Delivered"
            )
        }
    }

    override fun getItemCount() = orders.size

    fun updateList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    private fun showConfirmationDialog(
        holder: OrderViewHolder,
        order: Order,
        title: String,
        message: String,
        newStatus: String
    ) {
        MaterialAlertDialogBuilder(holder.itemView.context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                updateOrderStatus(order, newStatus, holder)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun updateOrderStatus(order: Order, newStatus: String, holder: OrderViewHolder) {
        db.collection("orders").document(order.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                order.status = newStatus
                holder.status.text = "Status: $newStatus"

                // Update button states
                when (newStatus) {
                    "Shipped" -> {
                        holder.btnShipped.isEnabled = false
                        holder.btnDelivered.isEnabled = true
                    }
                    "Delivered" -> {
                        holder.btnShipped.isEnabled = false
                        holder.btnDelivered.isEnabled = false
                    }
                }

                Toast.makeText(
                    holder.itemView.context,
                    "Order marked as $newStatus",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    holder.itemView.context,
                    "Failed to update: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
