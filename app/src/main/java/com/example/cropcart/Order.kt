package com.example.cropcart

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val timestamp: Long = 0,
    var status: String = "Pending"
)
