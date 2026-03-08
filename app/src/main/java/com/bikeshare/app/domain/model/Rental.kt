package com.bikeshare.app.domain.model

data class Rental(
    val bikeNumber: Int,
    val lockCode: String?,
    val standName: String?,
    val rentedAt: String?,
)
