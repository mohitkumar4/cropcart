package com.example.cropcart.cart

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartItemAdapter
    private lateinit var totalPriceText: TextView
    private lateinit var btnPlaceOrder: Button

    private val cartList = mutableListOf<CartItem>()
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.totalPriceText)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        val backButton = findViewById<ImageView>(R.id.btnBack)

        backButton.setOnClickListener {
            finish()
        }

        adapter = CartItemAdapter(
            cartList,
            onQuantityChanged = { item, newQuantity ->
                handleQuantityChange(item, newQuantity)
            },
            onItemRemoved = { item ->
                removeItemFromCart(item)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadCartFromFirestore()

        btnPlaceOrder.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
    }

    // 🧠 Load all cart items
    private fun loadCartFromFirestore() {
        db.collection("carts").document(userId)
            .get()
            .addOnSuccessListener { document ->
                cartList.clear()
                if (document.exists()) {
                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                    for (map in items) {
                        val item = CartItem(
                            id = map["id"] as String,
                            name = map["name"] as String,
                            image = map["image"] as String,
                            price = (map["price"] as Number).toDouble(),
                            quantity = (map["quantity"] as Number).toInt(),
                            sellerId = map["sellerId"] as String,
                            sellerName = map["sellerName"] as String
                        )
                        cartList.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
                updateTotalPrice(cartList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load cart: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 💸 Update total price dynamically
    private fun updateTotalPrice(cart: List<CartItem>) {
        val total = cart.sumOf { it.price * it.quantity }
        totalPriceText.text = "Total: ₹$total"
    }

    // ⚙️ Handle quantity changes (+ / -)
    private fun handleQuantityChange(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItemFromCart(item)
            return
        }

        // Update local list
        val index = cartList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            cartList[index].quantity = newQuantity
            adapter.notifyItemChanged(index)
        }

        // Update Firestore
        updateFirestoreCart()

        updateTotalPrice(cartList)
    }

    // 🗑 Remove an item completely
    private fun removeItemFromCart(item: CartItem) {
        cartList.removeAll { it.id == item.id }
        adapter.notifyDataSetChanged()
        updateFirestoreCart()
        updateTotalPrice(cartList)
        Toast.makeText(this, "${item.name} removed from cart", Toast.LENGTH_SHORT).show()
    }

    // 🧾 Sync cart with Firestore
    private fun updateFirestoreCart() {
        val updatedItems = cartList.map {
            hashMapOf(
                "id" to it.id,
                "name" to it.name,
                "image" to it.image,
                "price" to it.price,
                "quantity" to it.quantity,
                "sellerId" to it.sellerId,
                "sellerName" to it.sellerName
            )
        }

        db.collection("carts").document(userId)
            .set(hashMapOf("items" to updatedItems))
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update cart: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🛒 Place Order
    private fun placeOrder() {
        val orderData = hashMapOf(
            "userId" to userId,
            "items" to cartList.map {
                hashMapOf(
                    "id" to it.id,
                    "name" to it.name,
                    "image" to it.image,
                    "price" to it.price,
                    "quantity" to it.quantity,
                    "sellerName" to it.sellerName,
                    "sellerId" to it.sellerId
                )
            },
            "totalPrice" to cartList.sumOf { it.price * it.quantity },
            "timestamp" to System.currentTimeMillis(),
            "status" to "Pending"

        )

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                db.collection("carts").document(userId).delete()
                cartList.clear()
                adapter.notifyDataSetChanged()
                updateTotalPrice(cartList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun placeOrder() {
//        if (userId.isBlank()) {
//            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (cartList.isEmpty()) {
//            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Build items for the global order doc (include sellerId & sellerName per item)
//        val itemsForOrder = cartList.map { item ->
//            mapOf(
//                "id" to item.id,
//                "name" to item.name,
//                "image" to item.image,
//                "price" to item.price,
//                "quantity" to item.quantity,
//                "sellerId" to item.sellerId,
//                "sellerName" to item.sellerName
//            )
//        }
//
//        val total = cartList.sumOf { it.price * it.quantity }
//
//        val orderData = hashMapOf(
//            "userId" to userId,
//            "items" to itemsForOrder,
//            "totalPrice" to total,
//            "timestamp" to System.currentTimeMillis(),
//            "status" to "Pending"
//        )
//
//        // Firestore batch: create global order and per-seller copies
//        val db = FirebaseFirestore.getInstance()
//        val batch = db.batch()
//
//        // 1) Create new doc in 'orders' (server-generated id)
//        val ordersCol = db.collection("orders")
//        val newOrderRef = ordersCol.document() // generates ID (but not yet written)
//        batch.set(newOrderRef, orderData)
//
//        val orderId = newOrderRef.id
//
//        // 2) Group items by seller
//        val itemsBySeller = cartList.groupBy { it.sellerId }
//
//        // For each seller, create a seller-specific order doc under sellers/{sellerId}/orders/{orderId}
//        for ((sellerId, items) in itemsBySeller) {
//            if (sellerId.isBlank()) continue
//
//            val sellerItems = items.map { itItem ->
//                mapOf(
//                    "id" to itItem.id,
//                    "name" to itItem.name,
//                    "image" to itItem.image,
//                    "price" to itItem.price,
//                    "quantity" to itItem.quantity,
//                    "sellerId" to itItem.sellerId,
//                    "sellerName" to itItem.sellerName
//                )
//            }
//
//            val sellerOrderData = hashMapOf(
//                "orderId" to orderId,
//                "userId" to userId,
//                "items" to sellerItems,
//                "totalPrice" to sellerItems.sumOf { (it["price"] as Number).toDouble() * ((it["quantity"] as Number).toInt()) },
//                "timestamp" to System.currentTimeMillis(),
//                "status" to "Pending"
//            )
//
//            val sellerOrderRef = db.collection("sellers").document(sellerId)
//                .collection("orders").document(orderId)
//
//            batch.set(sellerOrderRef, sellerOrderData)
//        }
//
//        // Commit batch
//        batch.commit()
//            .addOnSuccessListener {
//                // Delete user's cart doc
//                db.collection("carts").document(userId).delete()
//                cartList.clear()
//                adapter.updateCart(emptyList())
//                updateTotalPrice(emptyList())
//                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

}
