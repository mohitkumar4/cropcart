package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.cropcart.account.ProfilePageActivity
import com.example.cropcart.address.ManageAddressesActivity
import com.example.cropcart.ai.AIChatActivity
import com.example.cropcart.cart.CartActivity
import com.example.cropcart.firebase.FirebaseRepo
import com.example.cropcart.gui.text.GuiRepo.setTiledBackground
import com.example.cropcart.information.InformationActivity
import com.example.cropcart.section.BuyFragment
import com.example.cropcart.seller.SellFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val db = FirebaseFirestore.getInstance()

    // views
    private lateinit var curve: View

    // constants
    private var buyTheme: Int = R.drawable.curve_background
    private var sellTheme: Int = R.drawable.curve_background_blue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<LinearLayout>(R.id.main).setTiledBackground(R.drawable.pattern_leaf, 0.25f, 0.05f)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        curve = findViewById<View>(R.id.curveBackground)
        val address = findViewById<LinearLayout>(R.id.addressCtn)

        address.setOnClickListener {
            startActivity(Intent(this, ManageAddressesActivity::class.java))
        }

        if(savedInstanceState == null) {
            db.collection(FirebaseRepo.Key.collectionUsers).document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        val type = document.getBoolean(FirebaseRepo.Key.type)
                        if (type == null) {
                            Toast.makeText(
                                this,
                                "Error: Cannot determine account type",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        }
                        if(type) loadFragment(BuyFragment(), buyTheme) else loadFragment(SellFragment(), sellTheme)
                    }
                    else Toast.makeText(this, "Error connecting to the database: Cannot find user", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "Error connecting to the database: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
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
        val newsIcon = menu.findItem(R.id.menu_information)
        val aiChatIcon = menu.findItem(R.id.menu_ai_chat)

        cartIcon.setIconTintList(getColorStateList(R.color.background_lighter))
        newsIcon.setIconTintList(getColorStateList(R.color.background_lighter))
        aiChatIcon.setIconTintList(getColorStateList(R.color.background_lighter))

        val initials = actionView?.findViewById<TextView>(R.id.profileInitials)

        Log.d("id", "${item.itemId}")
        Log.d("id", "${actionView?.id}")

        cartIcon.setOnMenuItemClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            true
        }
        newsIcon.setOnMenuItemClickListener{
            startActivity(Intent(this, InformationActivity::class.java))
            true
        }
        aiChatIcon.setOnMenuItemClickListener{
            startActivity(Intent(this, AIChatActivity::class.java))
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

    private fun loadFragment(fragment: Fragment, themeColor: Int){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        curve.background = ContextCompat.getDrawable(this, themeColor)
    }

    override fun onResume() {
        super.onResume()
        loadDefaultAddress()
    }
}