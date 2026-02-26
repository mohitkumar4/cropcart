package com.example.cropcart

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

class OrdersAdapter(
    private var ordersList: List<Order>
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderIdText)
        val orderDate: TextView = itemView.findViewById(R.id.orderDateText)
        val orderTotal: TextView = itemView.findViewById(R.id.orderTotalText)
        val orderItemsCount: TextView = itemView.findViewById(R.id.orderItemsCount)

        val orderStatus : TextView = itemView.findViewById(R.id.orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_card, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]
        holder.orderId.text = "Order #${order.id.takeLast(6)}"

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.orderDate.text = "Placed on: ${dateFormat.format(Date(order.timestamp))}"

        holder.orderTotal.text = "Total: ₹${order.totalPrice}"
        holder.orderItemsCount.text = "${order.items.size} items"

        holder.orderStatus.text = "Status: ${order.status}"

        when (order.status) {
            "Pending" -> holder.orderStatus.setTextColor("#FFA000".toColorInt()) // orange
            "Confirmed" -> holder.orderStatus.setTextColor("#2196F3".toColorInt()) // blue
            "Dispatched" -> holder.orderStatus.setTextColor("#673AB7".toColorInt()) // purple
            "Delivered" -> holder.orderStatus.setTextColor("#4CAF50".toColorInt()) // green
            "Cancelled" -> holder.orderStatus.setTextColor("#F44336".toColorInt()) // red
        }

    }

    override fun getItemCount() = ordersList.size

    fun updateList(newOrders: List<Order>) {
        ordersList = newOrders
        notifyDataSetChanged()
    }
}