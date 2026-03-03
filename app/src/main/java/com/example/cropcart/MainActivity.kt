package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cropcart.account.ProfilePageActivity
import com.example.cropcart.address.ManageAddressesActivity
import com.example.cropcart.cart.CartActivity
import com.example.cropcart.firebase.FirebaseRepo
import com.example.cropcart.section.BuyFragment
import com.example.cropcart.seller.SellFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
        val curve = findViewById<View>(R.id.curveBackground)
        val address = findViewById<LinearLayout>(R.id.addressCtn)

        address.setOnClickListener {
            startActivity(Intent(this, ManageAddressesActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Set LEFT and RIGHT padding only — NO BOTTOM padding
            view.setPadding(systemBarsInsets.left, 0, systemBarsInsets.right, 0)

            insets
        }


        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, BuyFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.buy -> {
                    curve.background = ContextCompat.getDrawable(this, R.drawable.curve_background)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, BuyFragment())
                        .commit()

                    true
                }
                R.id.sell -> {
                    curve.background = ContextCompat.getDrawable(this, R.drawable.curve_background_blue)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, SellFragment())
                        .commit()
                    true
                }
                else -> false
            }

        }

//        val productRecyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.productsRecyclerView)
//        val organicRecyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.OrganicProductsRecyclerView)

//        organicRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//
//        productRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


//
//        val sampleItems = listOf(
//            featured_product(null,"$2.99 / kg", "Organic, freshly picked", "description here"),
//            featured_product(null, "$3.50 / kg", "Crisp and juicy", "description here"),
//            featured_product(null, "$1.80 / kg", "Sweet & fresh", "description here"),
//        )
//
//        val adapter = FeaturedproductAdapter(sampleItems) { item ->
//            // Handle item click here
//            Toast.makeText(this, "Clicked on ${item.name}", Toast.LENGTH_SHORT).show()
//        }

//        productRecyclerView.adapter = adapter
//
//        organicRecyclerView.adapter = adapter

        setSupportActionBar(toolbar)

        loadDefaultAddress()



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
//                startActivity(Intent(this, ProfilePageActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.menu_profile)
        val actionView = item.actionView
        val profileIcon = actionView?.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileIcon)
        val cartIcon = menu.findItem(R.id.menu_cart)

        val initials = actionView?.findViewById<TextView>(R.id.profileInitials)

        Log.d("id", "${item.itemId}")
        Log.d("id", "${actionView?.id}")

        cartIcon.setOnMenuItemClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            true
        }

    profileIcon?.let {
        var userName = "Master User"
        val userProfileUrl: String? = null // replace with real URL if available

        db.collection(FirebaseRepo.Key.collectionUsers).document(userId).get().addOnSuccessListener {
            userName = it[FirebaseRepo.Key.username] as String

            if (!userProfileUrl.isNullOrEmpty()) {
                // Show profile picture
                initials?.visibility = View.GONE
                profileIcon.visibility = View.VISIBLE


                Glide.with(this)
                    .load("https://example.com/user/profile.jpg")  // user’s pfp URL
                    .placeholder(R.drawable.person)       // fallback
                    .into(profileIcon)

            } else {
                // Show initials
                profileIcon.visibility = View.GONE
                initials?.visibility = View.VISIBLE

                fun getInitials(name: String): String {
                    val parts = name.trim().split(" ")
                    return if (parts.size == 1) {
                        parts[0].first().uppercaseChar().toString()
                    } else {
                        "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
                    }
                }

                initials?.text = getInitials(userName)
            }
        }
            .addOnFailureListener {
                Log.e("userName Error", it.message.toString())
                userName = "Master User"
            }

    }



//        profileIcon?.setOnClickListener {
//
////            startActivity(Intent(this, ProfilePageActivity::class.java))
//            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
//
//        }

        actionView?.setOnClickListener {
             startActivity(Intent(this, ProfilePageActivity::class.java))
        }


        return super.onPrepareOptionsMenu(menu)
    }

    private fun loadDefaultAddress() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val addresses = doc[FirebaseRepo.Key.addresses] as? List<Map<String, Any>> ?: return@addOnSuccessListener
                val defaultAddress = addresses.find { it["isDefault"] == true }
                findViewById<TextView>(R.id.addressText).text =
                    defaultAddress?.get("addressLine")?.toString() ?: "No address selected"
            }
    }

    override fun onResume() {
        super.onResume()
        loadDefaultAddress()
    }
}