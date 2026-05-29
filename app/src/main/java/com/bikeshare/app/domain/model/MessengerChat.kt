package com.bikeshare.app.domain.model

data class MessengerChat(
    val name: String,
    val url: String,
    val icon: MessengerIcon,
)

enum class MessengerIcon(val slug: String) {
    TELEGRAM("telegram"),
    WHATSAPP("whatsapp"),
    SIGNAL("signal"),
    VIBER("viber"),
    DISCORD("discord"),
    GENERIC("generic");

    companion object {
        fun fromSlug(slug: String?): MessengerIcon =
            entries.firstOrNull { it.slug == slug?.lowercase() } ?: GENERIC
    }
}
