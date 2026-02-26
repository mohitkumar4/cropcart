package com.example.cropcart

data class FeaturedProduct(
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val section: String = "",
    val image: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val quantity: Int = 0,
    val sellerId: String = "",
    val sellerName: String = "",
)
