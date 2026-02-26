package com.example.cropcart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(
    private val addressList: List<Address>,
    private val onSelect: (Address) -> Unit,
    private val onEdit: (Address) -> Unit,
    private val onDelete: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressText: TextView = view.findViewById(R.id.addressText)
        val btnSelect: Button = view.findViewById(R.id.btnSelectAddress)
        val btnEdit: Button = view.findViewById(R.id.btnEditAddress)

        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.addressText.text =
            "${address.label}: ${address.addressLine}, ${address.city}, ${address.state}, ${address.pincode}"

        holder.btnSelect.text = if (address.isDefault) "Selected" else "Select"
        holder.btnSelect.isEnabled = !address.isDefault

        holder.btnSelect.setOnClickListener { onSelect(address) }
        holder.btnEdit.setOnClickListener { onEdit(address) }
        holder.btnDelete.setOnClickListener { onDelete(address) }
    }

    override fun getItemCount(): Int = addressList.size
}