package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_profile")
data class BrandProfile(
    @PrimaryKey val id: Int = 1,
    val brandName: String = "My Brand",
    val shopName: String = "My Shop",
    val mobileNumber: String = "+91 98765 43210",
    val email: String = "info@mybrand.com",
    val address: String = "123, Business Hub, Ahmedabad, Gujarat",
    val socialHandle: String = "@mybrand_poster365",
    val website: String = "www.mybrand.com",
    val qrText: String = "https://poster365.com/mybrand",
    val tagline: String = "Every Day Has a Story, Every Story Has a Design",
    val logoType: String = "geometric_circle", // geometric_circle, geometric_square, shield, flower, custom
    val primaryColorHex: String = "#E65100", // Default orange
    val textColorHex: String = "#FFFFFF" // Default white
)
