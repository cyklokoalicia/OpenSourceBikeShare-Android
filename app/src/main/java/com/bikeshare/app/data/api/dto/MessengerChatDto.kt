package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessengerChatDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String,
    @Json(name = "icon") val icon: String? = null,
)
