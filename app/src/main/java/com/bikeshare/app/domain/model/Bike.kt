package com.bikeshare.app.domain.model

data class Bike(
    val bikeNumber: Int,
    val currentUser: String?,
    val currentStand: String?,
    val note: String?,
)
