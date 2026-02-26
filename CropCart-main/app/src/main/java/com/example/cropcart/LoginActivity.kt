package com.example.cropcart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
                return@setOnClickListener
            }

           loginUser(email, password)

        }

        val notregistered = findViewById<TextView>(R.id.notregistered)

        notregistered.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginUser(email: String, password: String)
    {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->

                if(task.isSuccessful)
                {
                    val userId = mAuth.currentUser?.uid

                    if(userId != null)
                    {
                        fetchUserData(userId)
                    }
                }
                else
                {
                    progressBar.visibility = ProgressBar.INVISIBLE
                    Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "Login failed: ${task.exception?.message}", task.exception)
                }
            }
    }

    private fun fetchUserData(userId : String)
    {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if(document.exists())
                {
                    val username = document.getString("username")
                    Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            .addOnFailureListener {

                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()

            }
    }

    private fun hideKeyboard() {
        // Get the currently focused view
        val view = currentFocus
        if (view != null) {
            // Get the InputMethodManager system service
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // Hide the keyboard
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}