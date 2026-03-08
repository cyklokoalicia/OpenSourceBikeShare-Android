package com.bikeshare.app.domain.model

data class User(
    val userId: Int,
    val userName: String?,
    val phone: String?,
    val email: String?,
    val city: String?,
    val privileges: Int,
    val credit: Double?,
    val limit: Int?,
)
