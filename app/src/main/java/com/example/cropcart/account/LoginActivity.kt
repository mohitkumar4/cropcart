package com.example.cropcart.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cropcart.R
import com.example.cropcart.firebase.FirebaseRepo
import com.example.cropcart.gui.text.GuiRepo.setTiledBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        findViewById<LinearLayout>(R.id.login_layout).setTiledBackground(R.drawable.pattern_leaf, 0.25f, 0.05f)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar2)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            progressBar.visibility = ProgressBar.VISIBLE

            hideKeyboard()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
                return@setOnClickListener
            }
           loginUser(email, password)
        }

        val notregistered = findViewById<TextView>(R.id.notregistered)

        notregistered.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                progressBar.visibility = ProgressBar.INVISIBLE
                if(task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    if(userId != null) fetchUserData(userId)
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Login failed"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "Login error", task.exception)
                }
            }
    }

    private fun fetchUserData(userId : String){
        db.collection(FirebaseRepo.Key.collectionUsers).document(userId).get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    val username = document.getString(FirebaseRepo.Key.username)
                    Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, AccountTypeSelection::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.INVISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }
    }

    private fun hideKeyboard() {
        // Get the currently focused view
        val view = currentFocus
        if (view != null) {
            // Get the InputMethodManager system service
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            // Hide the keyboard
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}