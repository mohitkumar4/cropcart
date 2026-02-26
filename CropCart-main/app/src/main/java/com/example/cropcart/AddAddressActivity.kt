package com.example.cropcart

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddAddressActivity : AppCompatActivity() {

    private lateinit var labelInput: EditText
    private lateinit var addressLineInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var stateInput: EditText
    private lateinit var pincodeInput: EditText
    private lateinit var btnSave: Button

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        labelInput = findViewById(R.id.inputLabel)
        addressLineInput = findViewById(R.id.inputAddressLine)
        cityInput = findViewById(R.id.inputCity)
        stateInput = findViewById(R.id.inputState)
        pincodeInput = findViewById(R.id.inputPincode)
        btnSave = findViewById(R.id.btnSaveAddress)
        val backBtn = findViewById<ImageView>(R.id.btnBack)

        backBtn.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            saveAddress()
        }
    }

    private fun saveAddress() {
        val label = labelInput.text.toString().trim()
        val addressLine = addressLineInput.text.toString().trim()
        val city = cityInput.text.toString().trim()
        val state = stateInput.text.toString().trim()
        val pincode = pincodeInput.text.toString().trim()

        if (label.isEmpty() || addressLine.isEmpty() || city.isEmpty() || state.isEmpty() || pincode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newAddress = hashMapOf(
            "label" to label,
            "addressLine" to addressLine,
            "city" to city,
            "state" to state,
            "pincode" to pincode,
            "isDefault" to false
        )

        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { doc ->
            val existingAddresses = (doc.get("addresses") as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()
            existingAddresses.add(newAddress)

            userRef.update("addresses", existingAddresses)
                .addOnSuccessListener {
                    Toast.makeText(this, "Address added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add address: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}