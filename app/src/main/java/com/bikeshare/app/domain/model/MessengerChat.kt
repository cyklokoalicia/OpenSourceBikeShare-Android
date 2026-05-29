package com.bikeshare.app.domain.model

import java.util.Locale

data class MessengerChat(
    val name: String,
    val url: String,
    val icon: MessengerIcon,
)

enum class MessengerIcon {
    TELEGRAM,
    WHATSAPP,
    SIGNAL,
    VIBER,
    DISCORD,
    GENERIC;

    companion object {
        fun fromSlug(slug: String?): MessengerIcon {
            val normalized = slug?.lowercase(Locale.ROOT) ?: return GENERIC
            return entries.firstOrNull { it.name.lowercase(Locale.ROOT) == normalized } ?: GENERIC
        }
    }
}
