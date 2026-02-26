package com.example.cropcart

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orders)

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        val backBtn = findViewById<ImageView>(R.id.btnBack)

        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        ordersAdapter = OrdersAdapter(emptyList())
        ordersRecyclerView.adapter = ordersAdapter

        backBtn.setOnClickListener { finish() }

        loadOrdersRealtime()
    }

    // 🧠 Use addSnapshotListener for live updates
    private fun loadOrdersRealtime() {
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val ordersList = mutableListOf<Order>()

                    for (doc in snapshot.documents) {
                        val items = (doc["items"] as? List<Map<String, Any>>)?.map { map ->
                            CartItem(
                                id = map["id"] as String,
                                name = map["name"] as String,
                                image = map["image"] as String,
                                price = (map["price"] as Number).toDouble(),
                                quantity = (map["quantity"] as Number).toInt(),
                                sellerName = map["sellerName"] as? String ?: "",
                                sellerId = map["sellerId"] as? String ?: ""
                            )
                        } ?: emptyList()

                        val order = Order(
                            id = doc.id,
                            userId = doc["userId"] as String,
                            items = items,
                            totalPrice = (doc["totalPrice"] as Number).toDouble(),
                            timestamp = (doc["timestamp"] as Number).toLong(),
                            status = doc["status"] as? String ?: "Pending"
                        )

                        ordersList.add(order)
                    }

                    ordersAdapter.updateList(ordersList)
                }
            }
    }
}
