package com.example.cropcart

data class CartItem(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val price: Double = 0.0,
    var quantity: Int = 1,
    val sellerId: String = "",
    val sellerName: String = ""
)

