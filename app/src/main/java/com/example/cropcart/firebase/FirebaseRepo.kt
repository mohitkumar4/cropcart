package com.example.cropcart.firebase

import com.example.cropcart.cart.CartItem
import com.example.cropcart.product.FeaturedProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseRepo {
    object Key{
        val collectionUsers: String = "users"
        val collectionProducts: String = "products"
        val collectionOrders: String = "orders"
        val type: String = "type"
        val username: String = "username"
        val email: String = "email"
        val addresses: String = "addresses"
    }

    fun getProducts(callback: (Boolean, String, List<FeaturedProduct>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseRepo.Key.collectionProducts)
            .get()
            .addOnSuccessListener { result ->
                val products = mutableListOf<FeaturedProduct>()
                try {
                    for (document in result) {
                        val product = FeaturedProduct(
                            id = document.id,
                            name = document.getString("name") ?: "Unnamed",
                            category = document.getString("category") ?: "Others",
                            section = document.getString("section") ?: "Misc",
                            image = document.getString("image") ?: "",
                            price = document.getDouble("price") ?: 0.0
                        )
                        products.add(product)
                    }
                    callback(true, "", products)
                } catch (e: Exception) {
                    products.clear()
                    callback(false, "Parsing error: ${e.message}", products)
                }
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Unknown Error", listOf())
            }
    }

    fun getCartItems(callback: (Boolean, String, List<CartItem>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val cart: MutableList<CartItem> = mutableListOf()
        db.collection("carts").document(userId)
            .get()
            .addOnSuccessListener { document ->
                cart.clear()
                if (!document.exists()) {
                    callback(false, "You may have no products added to your cart", cart)
                    return@addOnSuccessListener
                }
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
                    cart.add(item)
                }
                callback(true, "", cart)
            }
    }
}