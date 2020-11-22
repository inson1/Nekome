package com.chesire.nekome.kitsu.search

import com.chesire.nekome.kitsu.api.intermediaries.Links
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Class to parse a response from [KitsuSearchService] into an object.
 */
@JsonClass(generateAdapter = true)
data class SearchData(
    @Json(name = "data")
    val data: List<KitsuSearchEntity>,
    @Json(name = "links")
    val links: Links
)
