package com.bikeshare.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Generic success envelope: { data: T, meta?: { requestId, timestamp } } — meta optional for resilience */
@JsonClass(generateAdapter = true)
data class ApiEnvelope<T>(
    @Json(name = "data") val data: T,
    @Json(name = "meta") val meta: ResponseMeta? = null,
)

@JsonClass(generateAdapter = true)
data class ResponseMeta(
    @Json(name = "requestId") val requestId: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
)

/**
 * application/problem+json error body. All fields are optional: a problem+json may omit
 * any RFC-7807 member, and we must still surface whatever it carries. With the previous
 * non-null fields, Moshi threw on any missing member, so a partial body made the caller
 * discard `detail`/`code` and fall back to a raw "HTTP <status>" line (spec 0011).
 */
@JsonClass(generateAdapter = true)
data class ProblemDetail(
    @Json(name = "type") val type: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "status") val status: Int? = null,
    @Json(name = "detail") val detail: String? = null,
    @Json(name = "instance") val instance: String? = null,
    @Json(name = "requestId") val requestId: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "params") val params: Map<String, Any?>? = null,
)
