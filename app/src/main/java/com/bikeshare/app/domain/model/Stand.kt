package com.bikeshare.app.domain.model

data class Stand(
    val standName: String,
    val description: String?,
    val photo: String?,
    val latitude: Double,
    val longitude: Double,
    val bikeCount: Int,
)
