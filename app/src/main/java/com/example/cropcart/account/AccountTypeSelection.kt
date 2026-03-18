package com.example.cropcart.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcart.MainActivity
import com.example.cropcart.R
import com.example.cropcart.firebase.FirebaseRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AccountTypeSelection : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private lateinit var progressBar: ProgressBar
    private lateinit var checkerCtn: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc_type_selection)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        checkerCtn = findViewById<LinearLayout>(R.id.checkerCtn)

        db.collection(FirebaseRepo.Key.collectionUsers).document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    val type = document.getBoolean(FirebaseRepo.Key.type)
                    if (type == null) promptAccountType()
                    else next()
                }
                else Toast.makeText(this, "Error connecting to the database: Cannot find user", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error connecting to the database: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun promptAccountType(){
        checkerCtn.visibility = View.GONE
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val farmerCtn: LinearLayout = findViewById<LinearLayout>(R.id.farmerCtn)
        val imgFarmer: ImageView = findViewById<ImageView>(R.id.imgFarmer)

        val customerCtn: LinearLayout = findViewById<LinearLayout>(R.id.customerCtn)
        val imgCustomer: ImageView = findViewById<ImageView>(R.id.imgCustomer)

        farmerCtn.startAnimation(animFadeIn)
        customerCtn.startAnimation(animFadeIn)

        imgFarmer.setOnClickListener { attemptSetAccountType(false) }
        imgCustomer.setOnClickListener { attemptSetAccountType(true) }
    }

    private fun attemptSetAccountType(isCustomer: Boolean) {
        progressBar.visibility = View.VISIBLE
        val data = mapOf(FirebaseRepo.Key.type to isCustomer)
        db.collection(FirebaseRepo.Key.collectionUsers)
            .document(userId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { next() }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Cannot set account type:\n${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }
    }

    private fun next(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}