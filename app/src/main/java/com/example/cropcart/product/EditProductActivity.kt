package com.example.cropcart.product

import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cropcart.R
import com.example.cropcart.product.ProductRepo.Sections
import com.google.firebase.firestore.FirebaseFirestore

class EditProductActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nameField: EditText
    private lateinit var priceField: EditText
    private lateinit var radioCategoryGroup: RadioGroup
    private lateinit var radioSectionGroup: RadioGroup
    private lateinit var quantityField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var imageUrlField: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var productId: String? = null
    private var existingImageUrl: String? = null

    private lateinit var selectedCategory: ProductRepo.Category
    private var selectedSection: String = ProductRepo.Sections.others

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        setupKeyboardAvoidance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

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
        radioCategoryGroup = findViewById(R.id.radioCategoryGroup)

        selectedCategory = ProductRepo.Categories.others
        radioCategoryGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedCategory = when (checkedId) {
                R.id.radioAll -> ProductRepo.Categories.all
                R.id.radioCereals -> ProductRepo.Categories.cereals
                R.id.radioPulses -> ProductRepo.Categories.pulses
                R.id.radioVegetables -> ProductRepo.Categories.vegetables
                R.id.radioFruits -> ProductRepo.Categories.fruits
                R.id.radioOilSeeds -> ProductRepo.Categories.oilSeeds
                R.id.radioSpices -> ProductRepo.Categories.spices
                R.id.radioSeeds -> ProductRepo.Categories.seeds
                R.id.radioOthers -> ProductRepo.Categories.others
                else -> ProductRepo.Categories.others
            }
        }

        radioSectionGroup = findViewById(R.id.radioSectionGroup)
        radioSectionGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSection = when (checkedId) {
                R.id.radioFreshProduce -> Sections.freshProduce
                R.id.radioDried -> Sections.dried
                R.id.radioProcessed -> Sections.processed
                R.id.radioOrganic -> Sections.organic
                R.id.radioNonOrganic -> Sections.nonOrganic
                R.id.radioHybridSeeds -> Sections.hybridSeeds
                R.id.radioRawMaterial -> Sections.rawMaterial
                R.id.radioAnimalFeed -> Sections.animalFeed
                else -> Sections.others
            }
        }

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

                    val categoryId = when(product?.category) {
                        ProductRepo.Categories.all.name -> R.id.radioAll
                        ProductRepo.Categories.cereals.name -> R.id.radioCereals
                        ProductRepo.Categories.pulses.name -> R.id.radioPulses
                        ProductRepo.Categories.vegetables.name -> R.id.radioVegetables
                        ProductRepo.Categories.fruits.name -> R.id.radioFruits
                        ProductRepo.Categories.oilSeeds.name -> R.id.radioOilSeeds
                        ProductRepo.Categories.spices.name -> R.id.radioSpices
                        ProductRepo.Categories.seeds.name -> R.id.radioSeeds
                        ProductRepo.Categories.others.name -> R.id.radioOthers
                        else -> R.id.radioOthers
                    }
                    radioCategoryGroup.check(categoryId)

                    val sectionId = when(product?.section) {
                        Sections.freshProduce -> R.id.radioFreshProduce
                        Sections.dried -> R.id.radioDried
                        Sections.processed -> R.id.radioProcessed
                        Sections.organic -> R.id.radioOrganic
                        Sections.nonOrganic -> R.id.radioNonOrganic
                        Sections.hybridSeeds -> R.id.radioHybridSeeds
                        Sections.rawMaterial -> R.id.radioRawMaterial
                        Sections.animalFeed -> R.id.radioAnimalFeed
                        else -> R.id.radioOthers
                    }
                    radioSectionGroup.check(sectionId)

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
        val price = priceField.text.toString().trim()
        val category = selectedCategory.name.trim()
        val section = selectedSection
        val quantity = quantityField.text.toString().trim()
        val description = descriptionField.text.toString().trim()
        val imageUrl = imageUrlField.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || category.isEmpty() || quantity.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val convertedPrice = price.toDoubleOrNull()
        if(convertedPrice == null || convertedPrice <= 0){
            Toast.makeText(this, "Price must be a number greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        val convertedQuantity = quantity.toIntOrNull()
        if(convertedQuantity == null || convertedQuantity <= 0){
            Toast.makeText(this, "Quantity must be a number greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "name" to name,
            "price" to convertedPrice,
            "category" to category,
            "section" to section,
            "quantity" to convertedQuantity,
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
