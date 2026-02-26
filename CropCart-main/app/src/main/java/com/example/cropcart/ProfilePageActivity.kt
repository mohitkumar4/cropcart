package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePageActivity : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_page)

        val back = findViewById<ImageView>(R.id.ivBack)

        val logoutButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.logoutButton)

        val profileIcon = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileIcon)

        val yourorders = findViewById<androidx.cardview.widget.CardView>(R.id.yourorders)

        val profileName = findViewById<TextView>(R.id.profileName)

        val yourAddresses = findViewById<androidx.cardview.widget.CardView>(R.id.youraddress)



        db.collection("users").document(userId).get().addOnSuccessListener {
            profileName.text = it["username"] as String
        }.addOnFailureListener {
            profileName.text = "Master User"
        }

        yourorders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        yourAddresses.setOnClickListener {
            startActivity(Intent(this, ManageAddressesActivity::class.java))
        }

        setUpPFP(profileIcon)



        logoutButton.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }


        back.setOnClickListener {

            finish()

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpPFP(profileIcon : de.hdodenhof.circleimageview.CircleImageView)
    {
        val initials = findViewById<TextView>(R.id.profileInitials)

//        val userName = "Master User"

        var userName = "Master User"

        Log.d("userid", userId)

        db.collection("users").document(userId).get().addOnSuccessListener {
            userName = it["username"] as String
            Log.d("Username", userName)

            val userProfileUrl: String? = null // replace with real URL if available

            if (!userProfileUrl.isNullOrEmpty()) {

                initials.visibility = View.GONE
                profileIcon.visibility = View.VISIBLE

                Glide.with(this)
                    .load("https://example.com/user/profile.jpg")  // user’s pfp URL
                    .placeholder(R.drawable.person)       // fallback
                    .into(profileIcon)

            }else {
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

        }.addOnFailureListener {

                Log.e("userName Error", it.message.toString())
                userName = "Master User"
            }

//        val userProfileUrl: String? = null // replace with real URL if available
//
//        if (!userProfileUrl.isNullOrEmpty()) {
//
//            initials.visibility = View.GONE
//            profileIcon.visibility = View.VISIBLE
//
//            Glide.with(this)
//                .load("https://example.com/user/profile.jpg")  // user’s pfp URL
//                .placeholder(R.drawable.person)       // fallback
//                .into(profileIcon)
//
//        }else {
//            // Show initials
//            profileIcon.visibility = View.GONE
//            initials?.visibility = View.VISIBLE
//
//            fun getInitials(name: String): String {
//                val parts = name.trim().split(" ")
//                return if (parts.size == 1) {
//                    parts[0].first().uppercaseChar().toString()
//                } else {
//                    "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
//                }
//            }
//
//            initials?.text = getInitials(userName)
//        }

    }


}