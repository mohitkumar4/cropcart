package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageAddressesActivity : AppCompatActivity() {

    private lateinit var addressRecyclerView: RecyclerView
    private lateinit var btnAddAddress: Button
    private lateinit var adapter: AddressAdapter

    private val addressList = mutableListOf<Address>()
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_addresses)

        addressRecyclerView = findViewById(R.id.addressRecyclerView)
        btnAddAddress = findViewById(R.id.btnAddAddress)
        val backButton = findViewById<ImageView>(R.id.btnBack)

        adapter = AddressAdapter(addressList,
            onSelect = { address -> setDefaultAddress(address) },
            onEdit = { address -> openEditAddress(address) },
            onDelete = { address -> deleteAddress(address) }
        )

        addressRecyclerView.layoutManager = LinearLayoutManager(this)
        addressRecyclerView.adapter = adapter

        backButton.setOnClickListener { finish() }

        btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }

        loadAddresses()
    }

    private fun loadAddresses() {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val list = doc["addresses"] as? List<Map<String, Any>> ?: emptyList()
                addressList.clear()
                for (map in list) {
                    addressList.add(
                        Address(
                            label = map["label"] as? String ?: "",
                            addressLine = map["addressLine"] as? String ?: "",
                            city = map["city"] as? String ?: "",
                            state = map["state"] as? String ?: "",
                            pincode = map["pincode"] as? String ?: "",
                            isDefault = map["isDefault"] as? Boolean ?: false // ✅ safe cast
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load addresses", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setDefaultAddress(address: Address) {
        // Turn all false, then set selected one true
        addressList.forEach { it.isDefault = false }
        address.isDefault = true

        val addressMapList = addressList.map {
            mapOf(
                "label" to it.label,
                "addressLine" to it.addressLine,
                "city" to it.city,
                "state" to it.state,
                "pincode" to it.pincode,
                "isDefault" to it.isDefault
            )
        }

        db.collection("users").document(userId)
            .update("addresses", addressMapList)
            .addOnSuccessListener {
                Toast.makeText(this, "Default address updated", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update default address", Toast.LENGTH_SHORT).show()
            }
    }


    private fun openEditAddress(address: Address) {
        val intent = Intent(this, EditAddressActivity::class.java)
        intent.putExtra("address", address)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadAddresses()
    }

    private fun deleteAddress(address: Address) {
        // Confirm before deleting
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Address")
            .setMessage("Are you sure you want to delete this address?")
            .setPositiveButton("Delete") { _, _ ->
                addressList.remove(address)

                if (address.isDefault && addressList.isNotEmpty()) {
                    addressList[0].isDefault = true
                }

                val addressMapList = addressList.map {
                    mapOf(
                        "label" to it.label,
                        "addressLine" to it.addressLine,
                        "city" to it.city,
                        "state" to it.state,
                        "pincode" to it.pincode,
                        "isDefault" to it.isDefault
                    )
                }

                db.collection("users").document(userId)
                    .update("addresses", addressMapList)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete address", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}