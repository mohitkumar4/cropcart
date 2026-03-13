package com.example.cropcart.seller

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.cart.CartItem
import com.example.cropcart.firebase.FirebaseRepo
import com.example.cropcart.orders.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SellerOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SellerOrdersAdapter
    private val db = FirebaseFirestore.getInstance()
    private val sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seller_orders)

        recyclerView = findViewById(R.id.sellerOrdersRecyclerView)
        val backButton = findViewById<ImageView>(R.id.btnBack)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SellerOrdersAdapter(emptyList())
        recyclerView.adapter = adapter

        backButton.setOnClickListener { finish() }

        loadOrdersForSeller()
    }

    private fun loadOrdersForSeller() {
        db.collection(FirebaseRepo.Key.collectionOrders)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val sellerOrders = mutableListOf<Order>()

                for (doc in snapshot) {
                    val items = (doc["items"] as? List<Map<String, Any>>)?.map { map ->
                        CartItem(
                            id = map["id"] as String,
                            name = map["name"] as String,
                            image = map["image"] as String,
                            price = (map["price"] as Number).toDouble(),
                            quantity = (map["quantity"] as Number).toInt(),
                            sellerId = map["sellerId"] as? String ?: "",
                            sellerName = map["sellerName"] as? String ?: ""
                        )
                    } ?: emptyList()

                    val sellerItems = items.filter { it.sellerId == sellerId }

                    val order = Order(
                        id = doc.id,
                        userId = doc["userId"] as String,
                        items = sellerItems,
                        totalPrice = sellerItems.sumOf { it.price * it.quantity },
                        timestamp = (doc["timestamp"] as Number).toLong(),
                        status = doc["status"] as? String ?: "Pending"
                    )


                    // ✅ Keep the order if *any* item belongs to this seller
                    if (items.any { it.sellerId == sellerId }) {
                        sellerOrders.add(order)
                    }
                }

                if (sellerOrders.isEmpty()) {
                    Toast.makeText(this, "No orders found for you.", Toast.LENGTH_SHORT).show()
                }

                adapter.updateList(sellerOrders)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load orders: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}