package com.example.cropcart.section

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.search.SearchActivity
import com.example.cropcart.product.FeaturedProduct
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BuyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BuyFragment : Fragment() {

    lateinit var sectionAdapter : SectionAdapter

    private var allProducts = mutableListOf<FeaturedProduct>()

    val db = FirebaseFirestore.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_buy, container, false)
        val sectionsRecyclerView = view.findViewById<RecyclerView>(R.id.sectionsRecyclerView)

//        val productRecyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.productsRecyclerView)
//        val organicRecyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.OrganicProductsRecyclerView)

//        val categoryRecyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.categoryRecyclerView)

        val tabLayout = view.findViewById<TabLayout>(R.id.categoryTabLayout)

        val searchBar = view.findViewById<TextView>(R.id.searchInputLayout)
        searchBar.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }


//        categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

//        organicRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//        productRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        sectionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        sectionAdapter = SectionAdapter(emptyList())


        sectionsRecyclerView.adapter = sectionAdapter

        val categories = listOf(
            "All" to R.drawable.all_tab_selector,
            "Cereals" to R.drawable.tab_icon_cereals,
            "Pulses" to R.drawable.tab_icon_pulses,
            "Vegetables" to R.drawable.tab_icon_vegetables,
            "Fruits" to R.drawable.tab_icon_fruits,
            "Oilseeds" to R.drawable.tab_icon_oilseeds,
            "Spices" to R.drawable.tab_icon_spices,
            "Seeds" to R.drawable.tab_icon_seeds,
            "Others" to R.drawable.tab_icon_others
        )

        categories.forEach { (name, iconRes) ->
            tabLayout.addTab(tabLayout.newTab().setText(name).setIcon(iconRes))
        }

//        val allProducts = listOf(
//            // Fresh Produce
//            FeaturedProduct("1", "Red Apple", "Fruits", "Fresh Produce", "https://via.placeholder.com/150", 120.0),
//            FeaturedProduct("2", "Carrot", "Vegetables", "Fresh Produce", "https://via.placeholder.com/150", 50.0),
//            FeaturedProduct("3", "Tomato", "Vegetables", "Fresh Produce", "https://via.placeholder.com/150", 40.0),
//
//            // Dried
//            FeaturedProduct("4", "Dry Mango Slices", "Fruits", "Dried", "https://via.placeholder.com/150", 200.0),
//            FeaturedProduct("5", "Raisins", "Fruits", "Dried", "https://via.placeholder.com/150", 150.0),
//
//            // Processed
//            FeaturedProduct("6", "Wheat Flour", "Cereals", "Processed", "https://via.placeholder.com/150", 80.0),
//            FeaturedProduct("7", "Sunflower Oil", "Oilseeds", "Processed", "https://via.placeholder.com/150", 180.0),
//            FeaturedProduct("8", "Tomato Extract", "Vegetables", "Processed", "https://via.placeholder.com/150", 120.0),
//
//            // Organic
//            FeaturedProduct("9", "Organic Spinach", "Vegetables", "Organic", "https://via.placeholder.com/150", 90.0),
//            FeaturedProduct("10", "Organic Almonds", "Nuts", "Organic", "https://via.placeholder.com/150", 350.0),
//
//            // Non-organic
//            FeaturedProduct("11", "Regular Potato", "Vegetables", "Non-organic", "https://via.placeholder.com/150", 30.0),
//            FeaturedProduct("12", "Regular Banana", "Fruits", "Non-organic", "https://via.placeholder.com/150", 40.0),
//
//            // Hybrid Seeds
//            FeaturedProduct("13", "Hybrid Corn Seeds", "Cereals", "Hybrid Seeds", "https://via.placeholder.com/150", 250.0),
//            FeaturedProduct("14", "Hybrid Tomato Seeds", "Vegetables", "Hybrid Seeds", "https://via.placeholder.com/150", 200.0),
//
//            // Raw Material
//            FeaturedProduct("15", "Raw Cocoa Beans", "Processed", "Raw Material", "https://via.placeholder.com/150", 500.0),
//            FeaturedProduct("16", "Raw Wheat", "Cereals", "Raw Material", "https://via.placeholder.com/150", 70.0),
//
//            // Animal Feed Quality
//            FeaturedProduct("17", "Corn for Cattle", "Cereals", "Animal Feed Quality", "https://via.placeholder.com/150", 90.0),
//            FeaturedProduct("18", "Soybean Meal", "Pulses", "Animal Feed Quality", "https://via.placeholder.com/150", 120.0)
//        )

        fetchProductsFromFirestore()

        updateSections(allProducts) // ✅ show products when fragment starts


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedCategory = tab.text.toString()

                val filteredProducts = if (selectedCategory == "All") {
                    allProducts
                } else {
                    allProducts.filter { it.category == selectedCategory }
                }

                updateSections(filteredProducts)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


//        tabLayout.addTab(tabLayout.newTab().setText("All").setIcon(R.drawable.all_tab_selector))

//        for (category in categories) {
//            tabLayout.addTab(tabLayout.newTab().setText(category))
//        }
//
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val selectedCategory = tab.text.toString()
//                // 🔥 Filter your productsRecyclerView data here based on selectedCategory
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })



//        val categoryAdapter = CategoryAdapter(categories) {item->
//            Toast.makeText(requireContext(), "Clicked on $item", Toast.LENGTH_SHORT).show()
//        }

//        val adapter = FeaturedproductAdapter(sampleItems) { item ->
//            // Handle item click here
//            Toast.makeText(requireContext(), "Clicked on ${item.name}", Toast.LENGTH_SHORT).show()
//        }

//        categoryRecyclerView.adapter = categoryAdapter

//        productRecyclerView.adapter = adapter
//
//        organicRecyclerView.adapter = adapter

        return view
    }

    private fun fetchProductsFromFirestore() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = mutableListOf<FeaturedProduct>()
                for (document in result) {
                    try {
                        val product = FeaturedProduct(
                            id = document.id,
                            name = document.getString("name") ?: "Unnamed",
                            category = document.getString("category") ?: "Others",
                            section = document.getString("section") ?: "Misc",
                            image = document.getString("image") ?: "",
                            price = document.getDouble("price") ?: 0.0
                        )
                        products.add(product)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error parsing product: ${e.message}")
                    }
                }

                allProducts = products
                updateSections(allProducts)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch products: ${e.message}")
            }
    }

    private fun updateSections(filteredProducts: List<FeaturedProduct>) {
        val sectionNames = listOf(
            "Fresh Produce", "Dried", "Processed",
            "Organic", "Non-organic", "Hybrid Seeds",
            "Raw Material", "Animal Feed Quality"
        )

        val sections = sectionNames.mapNotNull { section ->
            val productsInSection = filteredProducts.filter { it.section == section }
            if (productsInSection.isNotEmpty())
                Section(section, productsInSection)
            else null
        }

        sectionAdapter.submitList(sections)
    }


}