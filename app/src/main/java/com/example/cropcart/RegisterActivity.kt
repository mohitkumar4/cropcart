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

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.etPassword)
        val usernameField: EditText = findViewById(R.id.username)
        val registerBtn: Button = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar2)


        registerBtn.setOnClickListener {

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val username = usernameField.text.toString().trim()

            progressBar.visibility = ProgressBar.VISIBLE

            hideKeyboard()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = hashMapOf(
                            "username" to username,
                            "email" to email
                        )



                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                                finish()
                                Log.d("RegisterActivity", "Username saved successfully")
                            }
                            .addOnFailureListener {
                                Log.e("RegisterActivity", "Error saving username", it)
                                Toast.makeText(this, "Failed to save username", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        progressBar.visibility = ProgressBar.INVISIBLE
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }

        }


        val loginpage = findViewById<TextView>(R.id.loginpage)

        loginpage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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