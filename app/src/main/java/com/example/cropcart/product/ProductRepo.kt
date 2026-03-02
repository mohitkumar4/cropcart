package com.example.cropcart.product

import com.example.cropcart.R

object ProductRepo {
    data class Category(val name: String, val imageId: Int)

    object Categories{
        val all = Category("All", R.drawable.all_tab_selector)
        val cereals = Category("Cereals", R.drawable.tab_icon_cereals)
        val pulses = Category("Pulses", R.drawable.tab_icon_pulses)
        val vegetables = Category("Vegetables", R.drawable.tab_icon_vegetables)
        val fruits = Category("Fruits", R.drawable.tab_icon_fruits)
        val oilSeeds = Category("Oil Seeds", R.drawable.tab_icon_oilseeds)
        val spices = Category("Spices", R.drawable.tab_icon_spices)
        val seeds = Category("Seeds", R.drawable.tab_icon_seeds)
        val others = Category("Others", R.drawable.tab_icon_others)

        private val categories: List<Category> = listOf(all, cereals, pulses, vegetables, fruits, oilSeeds, spices, seeds, others)

        fun getCategory(categoryName: String?): Category {
            if (categoryName == null) return others
            return categories.firstOrNull { it.name == categoryName }?:others
        }

        fun getCategoryNames(): Array<String> = categories.map { it.name }.toTypedArray()
    }
}