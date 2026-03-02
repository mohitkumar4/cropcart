package com.example.cropcart.address

import java.io.Serializable

data class Address(
    var label: String = "",
    var addressLine: String = "",
    var city: String = "",
    var state: String = "",
    var pincode: String = "",
    var isDefault: Boolean = false
) : Serializable