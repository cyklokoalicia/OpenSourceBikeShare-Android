package com.bikeshare.app.ui.map

import com.bikeshare.app.data.api.dto.StandMarkerDto

/** Stand statuses that render as non-rideable "background" markers (service / testing). */
private const val STATUS_TECHNICAL = "technical"
private const val STATUS_HIDDEN = "hidden"

/**
 * Map layer for a stand by status (spec 0010): service ("technical") and testing
 * ("hidden") stands sit on a lower layer (0); active/parking stands on top (1), so an
 * active stand is never hidden behind a service marker. The single status→layer mapping.
 */
internal fun standDrawLayer(status: String?): Int =
    if (status == STATUS_TECHNICAL || status == STATUS_HIDDEN) 0 else 1

/**
 * Orders stands for map drawing: lower layers first. osmdroid draws later-added overlays
 * on top, so service/testing markers (layer 0) are added before — and sit below —
 * active markers (layer 1). Stable: fetch order is preserved within each layer.
 */
fun standsInDrawOrder(stands: List<StandMarkerDto>): List<StandMarkerDto> =
    stands.sortedBy { standDrawLayer(it.status) }
