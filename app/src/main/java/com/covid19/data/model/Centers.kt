package com.covid19.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Centers(
    val address: String,
    val centerName: String,
    val centerType: String,
    val createdAt: String,
    val facilityName: String,
    @PrimaryKey
    val id: Int,
    val lat: String,
    val lng: String,
    val org: String,
    val phoneNumber: String,
    val sido: String,
    val sigungu: String,
    val updatedAt: String,
    val zipCode: String
)
