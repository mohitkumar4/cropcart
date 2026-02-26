package com.example.cropcart

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddProductActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputDescription: EditText
    private lateinit var inputImage: EditText
    private lateinit var inputSection: EditText

    private lateinit var inputQuantity: EditText

    private lateinit var inputCategory: EditText
    private lateinit var btnSubmitProduct: Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

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

        inputName = findViewById(R.id.inputName)
        inputImage = findViewById(R.id.inputImage)
        inputCategory = findViewById(R.id.inputCategory)
        inputPrice = findViewById(R.id.inputPrice)
        inputDescription = findViewById(R.id.inputDescription)
        inputSection = findViewById(R.id.inputSection)
        btnSubmitProduct = findViewById(R.id.btnSubmitProduct)
        inputQuantity = findViewById(R.id.inputQuantity)
        val currentUser = auth.currentUser


        btnSubmitProduct.setOnClickListener {
            val name = inputName.text.toString().trim()
            val priceText = inputPrice.text.toString().trim()
            val description = inputDescription.text.toString().trim()
            val image = inputImage.text.toString().trim()
            val section = inputSection.text.toString().trim()
            val category = inputCategory.text.toString().trim()
            val quantityText = inputQuantity.text.toString().trim()



            if (name.isEmpty() || priceText.isEmpty() || description.isEmpty() || image.isEmpty() || section.isEmpty() || category.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = try {
                priceText.toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()



            val sellerId = currentUser!!.uid




            db.collection("users").document(sellerId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val sellerName = document.getString("username") ?: "Unknown Seller"

                        // Step 2: Now build product data
                        val product = hashMapOf(
                            "name" to name,
                            "category" to category,
                            "section" to section,
                            "image" to image,
                            "price" to price,
                            "description" to description,
                            "quantity" to quantity,
                            "sellerId" to sellerId,
                            "sellerName" to sellerName
                        )

                        // Step 3: Push to Firestore
                        db.collection("products")
                            .add(product)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Product added successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(this, "Seller record not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching seller info: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun clearFields() {
        inputName.text.clear()
        inputPrice.text.clear()
        inputDescription.text.clear()
        inputImage.text.clear()
        inputSection.text.clear()
    }

    private fun setupKeyboardAvoidance() {
        val scrollView = findViewById<ScrollView>(R.id.main)

        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = android.graphics.Rect()
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