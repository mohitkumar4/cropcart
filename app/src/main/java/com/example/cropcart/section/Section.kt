package com.example.cropcart.section

import com.example.cropcart.product.FeaturedProduct

data class Section(
    val title: String,
    val items: List<FeaturedProduct>
)
