package com.example.cropcart.search

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.GeminiSearchHelper
import com.example.cropcart.R
import com.example.cropcart.firebase.FirebaseRepo
import com.example.cropcart.product.FeaturedProduct
import com.example.cropcart.product.ProductDetailActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var productAdapter: SearchProductAdapter

    private val productList = ArrayList<FeaturedProduct>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        searchInput = findViewById(R.id.searchInput)
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)

        searchInput.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)

        resultsRecyclerView.layoutManager = GridLayoutManager(this, 2)

        productAdapter = SearchProductAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("productId", product.id)
            startActivity(intent)
        }
        resultsRecyclerView.adapter = productAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSearchButton()
    }

    private fun setupSearchButton() {
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    performAISearch(query)
                } else {
                    Toast.makeText(this, "Enter something to search", Toast.LENGTH_SHORT).show()
                }
                true
            } else false
        }
    }

    private fun performAISearch(query: String) {
        // Hide keyboard after pressing search
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)

        Toast.makeText(this, "Searching with AI…", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val filters = GeminiSearchHelper.parseSearchQuery(query)
                applyFilters(filters)
            } catch (e: Exception) {
                // If Gemini quota exceeded or API fails, fallback to normal search
                if (e.message?.contains("quota", true) == true) {
                    Toast.makeText(
                        this@SearchActivity,
                        "AI quota exceeded — using normal search",
                        Toast.LENGTH_LONG
                    ).show()
                    fallbackSearch(query)
                } else {
                    Toast.makeText(
                        this@SearchActivity,
                        "AI Search failed: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun applyFilters(filters: Map<String, Any?>) {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()

                for (doc in result) {
                    val product = doc.toObject(FeaturedProduct::class.java)
                    product.id = doc.id

                    val matchesCategory = filters["category"] == null ||
                            product.category.equals(filters["category"].toString(), ignoreCase = true)

                    val matchesSection = filters["section"] == null ||
                            product.section.equals(filters["section"].toString(), ignoreCase = true)

                    val matchesKeyword = filters["keywords"] == null ||
                            product.name.contains(filters["keywords"].toString(), ignoreCase = true)

                    val matchesPrice = when {
                        filters["price_min"] != null && filters["price_max"] != null ->
                            product.price >= filters["price_min"].toString().toDouble() &&
                                    product.price <= filters["price_max"].toString().toDouble()
                        filters["price_min"] != null ->
                            product.price >= filters["price_min"].toString().toDouble()
                        filters["price_max"] != null ->
                            product.price <= filters["price_max"].toString().toDouble()
                        else -> true
                    }

                    if (matchesCategory && matchesSection && matchesKeyword && matchesPrice) {
                        productList.add(product)
                    }
                }

                if (productList.isEmpty()) {
                    Toast.makeText(this, "No matching products found", Toast.LENGTH_SHORT).show()
                }

                productAdapter.updateList(productList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fallbackSearch(query: String) {
        val lowercaseQuery = query.lowercase()
        db.collection(FirebaseRepo.Key.collectionProducts)
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (doc in result) {
                    val product = doc.toObject(FeaturedProduct::class.java)
                    product.id = doc.id

                    if (product.name.lowercase().contains(lowercaseQuery)
                        || product.category.lowercase().contains(lowercaseQuery)
                        || product.section.lowercase().contains(lowercaseQuery)
                    ) {
                        productList.add(product)
                    }
                }

                if (productList.isEmpty()) {
                    Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
                }

                productAdapter.updateList(productList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
