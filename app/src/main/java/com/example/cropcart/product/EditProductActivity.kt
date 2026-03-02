package com.example.cropcart.product

import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cropcart.R
import com.google.firebase.firestore.FirebaseFirestore


class EditProductActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nameField: EditText
    private lateinit var priceField: EditText
    private lateinit var categoryField: EditText
    private lateinit var sectionField: EditText
    private lateinit var quantityField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var imageUrlField: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var productId: String? = null
    private var existingImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_product)

        setupKeyboardAvoidance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Add bottom padding equal to keyboard height when visible
            view.setPadding(
                sysInsets.left,
                sysInsets.top,
                sysInsets.right,
                maxOf(sysInsets.bottom, imeInsets.bottom)
            )

            WindowInsetsCompat.CONSUMED
        }

        imageView = findViewById(R.id.productImage)
        nameField = findViewById(R.id.editProductName)
        priceField = findViewById(R.id.editProductPrice)
        categoryField = findViewById(R.id.editProductCategory)
        sectionField = findViewById(R.id.editProductSection)
        quantityField = findViewById(R.id.editProductQuantity)
        descriptionField = findViewById(R.id.editProductDescription)
        imageUrlField = findViewById(R.id.editProductImageUrl)
        saveButton = findViewById(R.id.btnSaveChanges)

        productId = intent.getStringExtra("productId")

        if (productId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProductDetails()
        saveButton.setOnClickListener { saveProductChanges() }
    }

    private fun loadProductDetails() {
        db.collection("products").document(productId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val product = doc.toObject(FeaturedProduct::class.java)
                    nameField.setText(product?.name)
                    priceField.setText(product?.price.toString())
                    categoryField.setText(product?.category)
                    sectionField.setText(product?.section)
                    quantityField.setText(product?.quantity.toString())
                    descriptionField.setText(product?.description)
                    imageUrlField.setText(product?.image)
                    existingImageUrl = product?.image

                    Glide.with(this)
                        .load(existingImageUrl)
                        .placeholder(R.drawable.img)
                        .into(imageView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load product: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProductChanges() {
        val name = nameField.text.toString().trim()
        val price = priceField.text.toString().trim().toDoubleOrNull()
        val category = categoryField.text.toString().trim()
        val section = sectionField.text.toString().trim()
        val quantity = quantityField.text.toString().trim().toIntOrNull()
        val description = descriptionField.text.toString().trim()
        val imageUrl = imageUrlField.text.toString().trim()

        if (name.isEmpty() || price == null || category.isEmpty() || section.isEmpty() || quantity == null || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "section" to section,
            "quantity" to quantity,
            "description" to description,
            "image" to if (imageUrl.isNotEmpty()) imageUrl else existingImageUrl
        )

        db.collection("products").document(productId!!)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update product: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupKeyboardAvoidance() {
        val scrollView = findViewById<ScrollView>(R.id.main)

        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            scrollView.getWindowVisibleDisplayFrame(r)
            val screenHeight = scrollView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // if keyboard is visible
            if (keypadHeight > screenHeight * 0.15) {
                scrollView.post {
                    val focused = currentFocus
                    focused?.let {
                        scrollView.smoothScrollTo(0, it.bottom)
                    }
                }
            }
        }
    }

}
