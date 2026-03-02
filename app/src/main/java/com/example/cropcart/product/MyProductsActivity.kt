package com.example.cropcart.product

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyProductsAdapter
    private val db = FirebaseFirestore.getInstance()
    private val sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_products)

        recyclerView = findViewById(R.id.myProductsRecyclerView)
        val backButton = findViewById<ImageView>(R.id.btnBack)
        val addButton = findViewById<ImageView>(R.id.btnAddProduct)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyProductsAdapter(emptyList(),
            onEditClicked = { product ->
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("productId", product.id)
                startActivity(intent)
            },
            onDeleteClicked = { product ->
                showDeleteConfirmation(product)
            }
        )

        recyclerView.adapter = adapter
        backButton.setOnClickListener { finish() }
        addButton.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        loadMyProducts()
    }

    private fun loadMyProducts() {
        db.collection("products")
            .whereEqualTo("sellerId", sellerId)
            .get()
            .addOnSuccessListener { result ->
                val products = result.mapNotNull { doc ->
                    doc.toObject(FeaturedProduct::class.java).apply { id = doc.id }
                }
                adapter.updateList(products)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load products: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation(product: FeaturedProduct) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '${product.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                db.collection("products").document(product.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
                        loadMyProducts()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}