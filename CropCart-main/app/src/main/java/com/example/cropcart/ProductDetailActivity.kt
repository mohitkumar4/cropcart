package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailActivity : AppCompatActivity() {

    private var quantity = 0
    private var unitPrice = 1200.0

    private lateinit var bottomPrice: TextView
    private lateinit var btnAddToCart: Button
    private lateinit var quantityLayout: LinearLayout
    private lateinit var btnDecrease: TextView
    private lateinit var btnIncrease: TextView
    private lateinit var quantityText: TextView

    private lateinit var similarRecyclerView: RecyclerView
    private lateinit var similarAdapter: SimilarAdapter

    private lateinit var name: TextView
    private lateinit var price: TextView
    private lateinit var description: TextView
    private lateinit var sellerName: TextView
    private var currentProductSellerId: String? = null
    private lateinit var image: ImageView

   var imageUrl:String? = ""



    var currentProduct:String? = ""

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)

        name = findViewById<TextView>(R.id.productName)
        price = findViewById<TextView>(R.id.productPrice)
        description = findViewById<TextView>(R.id.productDescription)
        sellerName = findViewById<TextView>(R.id.sellerName)
        image = findViewById<ImageView>(R.id.productImage)

        val productId = intent.getStringExtra("productId")


        if(productId == null)
        {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }



        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val product = doc.toObject(FeaturedProduct::class.java)

                    name.text = product?.name
                    price.text = "₹ ${product?.price} / kg"
                    description.text = product?.description
                    currentProductSellerId = product?.sellerId
                    sellerName.text = "Seller: ${product?.sellerName}"
                    currentProduct = product?.category
                    imageUrl = product?.image

                    // ✅ Assign the real price
                    unitPrice = product?.price ?: 0.0
                    bottomPrice.text = "₹ $unitPrice / kg"

                    Glide.with(this)
                        .load(product?.image)
                        .placeholder(R.drawable.img)
                        .into(image)

                    // Load similar products
                    if (currentProduct != null) {
                        val allProducts = ArrayList<FeaturedProduct>()
                        db.collection("products").get().addOnSuccessListener { result ->
                            for (docItem in result) {
                                val prod = docItem.toObject(FeaturedProduct::class.java)
                                prod.id = docItem.id
                                if (prod.category == currentProduct && prod.id != productId) {
                                    allProducts.add(prod)
                                }
                            }
                            similarAdapter.updateList(allProducts)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading product: ${e.message}", Toast.LENGTH_SHORT).show()
            }



        bottomPrice = findViewById(R.id.bottomPrice)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        quantityLayout = findViewById(R.id.quantityLayout)
        btnDecrease = findViewById(R.id.btnDecrease)
        btnIncrease = findViewById(R.id.btnIncrease)
        quantityText = findViewById(R.id.quantityText)

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

        val allProducts = ArrayList<FeaturedProduct>()
//        db.collection("products").get().addOnSuccessListener {
//           result->
//            for(doc in result){
//                var product = doc.toObject(FeaturedProduct::class.java)
//                product.id = doc.id
//
//                if(product.category == currentProduct){
//                    allProducts.add(product)
//                }
//                println(product.name)
//            }
//            similarAdapter.updateList(allProducts)
//         }



        similarRecyclerView = findViewById(R.id.similarRecyclerView)
        similarAdapter = SimilarAdapter(emptyList()) { product ->
            // Open the clicked similar product
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("productId", product.id)
            startActivity(intent)
        }

        similarRecyclerView.apply {
            layoutManager = GridLayoutManager(this@ProductDetailActivity, 3)
            adapter = similarAdapter
        }



//        val currentProduct = FeaturedProduct("11", "Regular Potato", "Vegetables", "Non-organic", "https://via.placeholder.com/150", 30.0)


//        val similarProducts = allProducts.filter { it.category == currentProduct && it.id != productId }
//        similarAdapter.updateList(similarProducts)


        // Optionally read price dynamically from intent
        intent.getDoubleExtra("productPrice", unitPrice).let {
            unitPrice = it
            bottomPrice.text = "₹ $unitPrice / quintal"
        }

        // Add to cart click
        btnAddToCart.setOnClickListener {
            quantity = 1
            saveItemToCart()
            updateQuantityUI()
        }

        // Increase quantity
        btnIncrease.setOnClickListener {
            quantity++
            saveItemToCart()
            updateQuantityUI()
        }

        // Decrease quantity
        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                saveItemToCart()
                updateQuantityUI()
            } else {
                // Back to Add to Cart
                quantity = 0
                removeItemFromCart()
                quantityLayout.visibility = View.GONE
                btnAddToCart.visibility = View.VISIBLE
                bottomPrice.text = "₹ $unitPrice / quintal"
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateQuantityUI() {
        quantityLayout.visibility = View.VISIBLE
        btnAddToCart.visibility = View.GONE
        quantityText.text = quantity.toString()

        val totalPrice = unitPrice * quantity
        bottomPrice.text = "₹ $totalPrice / kg"
    }

    private fun saveItemToCart() {
        val cartRef = db.collection("carts").document(userId)

        val currentProductId = intent.getStringExtra("productId") ?: return
        val productName = name.text.toString()
        val productImageUrl = imageUrl ?: ""
        val productSellerId = currentProductSellerId ?: ""    // set this when loading product
        val productSellerName = sellerName.text ?: ""// set this when loading product

        val cartItem: Map<String, Any> = mapOf(
            "id" to currentProductId,
            "name" to productName,
            "image" to productImageUrl,
            "price" to unitPrice,
            "quantity" to quantity,
            "sellerId" to productSellerId,
            "sellerName" to productSellerName
        )

        cartRef.get().addOnSuccessListener { doc ->
            val items = (doc.get("items") as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()
            val existingIndex = items.indexOfFirst { it["id"] == currentProductId }
            if (existingIndex >= 0) {
                val existing = items[existingIndex].toMutableMap()
                existing["quantity"] = quantity
                items[existingIndex] = existing
            } else {
                items.add(cartItem)
            }

            cartRef.set(mapOf("items" to items))
                .addOnSuccessListener { Toast.makeText(this, "Cart updated", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show() }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to read cart: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun removeItemFromCart() {
        val productId = intent.getStringExtra("productId")

        val cartRef = db.collection("carts").document(userId)
        cartRef.get().addOnSuccessListener { doc ->
            val items = doc.get("items") as? MutableList<HashMap<String, Any>> ?: mutableListOf()
            val updatedItems = items.filter { it["id"] != productId }
            cartRef.set(hashMapOf("items" to updatedItems))
        }
    }


}