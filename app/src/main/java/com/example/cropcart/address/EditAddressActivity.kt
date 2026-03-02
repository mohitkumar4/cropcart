package com.example.cropcart.address

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcart.address.Address
import com.example.cropcart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditAddressActivity : AppCompatActivity() {

    private lateinit var labelInput: EditText
    private lateinit var addressLineInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var stateInput: EditText
    private lateinit var pincodeInput: EditText
    private lateinit var btnSaveChanges: Button

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var existingAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address) // Reuse same layout!

        labelInput = findViewById(R.id.inputLabel)
        addressLineInput = findViewById(R.id.inputAddressLine)
        cityInput = findViewById(R.id.inputCity)
        stateInput = findViewById(R.id.inputState)
        pincodeInput = findViewById(R.id.inputPincode)
        btnSaveChanges = findViewById(R.id.btnSaveAddress)
        val backBtn = findViewById<ImageView>(R.id.btnBack)

        backBtn.setOnClickListener { finish() }

        existingAddress = intent.getSerializableExtra("address") as? Address

        existingAddress?.let {
            labelInput.setText(it.label)
            addressLineInput.setText(it.addressLine)
            cityInput.setText(it.city)
            stateInput.setText(it.state)
            pincodeInput.setText(it.pincode)
        }

        btnSaveChanges.text = "Save Changes"
        btnSaveChanges.setOnClickListener {
            updateAddress()
        }
    }

    private fun updateAddress() {
        val label = labelInput.text.toString().trim()
        val addressLine = addressLineInput.text.toString().trim()
        val city = cityInput.text.toString().trim()
        val state = stateInput.text.toString().trim()
        val pincode = pincodeInput.text.toString().trim()

        if (label.isEmpty() || addressLine.isEmpty() || city.isEmpty() || state.isEmpty() || pincode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAddress = Address(
            label, addressLine, city, state, pincode, existingAddress?.isDefault ?: false
        )

        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { doc ->
            val addresses = (doc.get("addresses") as? MutableList<Map<String, Any>>)?.toMutableList()
                ?: mutableListOf()

            val index = addresses.indexOfFirst {
                it["label"] == existingAddress?.label &&
                        it["addressLine"] == existingAddress?.addressLine
            }

            if (index != -1) {
                addresses[index] = hashMapOf(
                    "label" to updatedAddress.label,
                    "addressLine" to updatedAddress.addressLine,
                    "city" to updatedAddress.city,
                    "state" to updatedAddress.state,
                    "pincode" to updatedAddress.pincode,
                    "isDefault" to updatedAddress.isDefault
                )

                userRef.update("addresses", addresses)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Address updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Address not found in Firestore", Toast.LENGTH_SHORT).show()
            }
        }
    }
}