package com.chesire.nekome.kitsu.search

import com.chesire.nekome.core.EntityMapper
import com.chesire.nekome.core.Resource
import com.chesire.nekome.kitsu.asError
import com.chesire.nekome.kitsu.parse
import com.chesire.nekome.search.api.SearchApi
import com.chesire.nekome.search.api.SearchEntity
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of the [SearchApi] for usage with the Kitsu API.
 */
class KitsuSearch @Inject constructor(
    private val searchService: KitsuSearchService,
    private val map: EntityMapper<KitsuSearchEntity, SearchEntity>
) : SearchApi {

    override suspend fun searchForAnime(title: String): Resource<List<SearchEntity>> {
        return try {
            parseResponse(searchService.searchForAnimeAsync(title))
        } catch (ex: Exception) {
            ex.parse()
        }
    }

    override suspend fun searchForManga(title: String): Resource<List<SearchEntity>> {
        return try {
            parseResponse(searchService.searchForMangaAsync(title))
        } catch (ex: Exception) {
            ex.parse()
        }
    }

    private fun parseResponse(response: Response<SearchData>): Resource<List<SearchEntity>> {
        return if (response.isSuccessful) {
            response.body()?.let { search ->
                Resource.Success(search.data.map { map.from(it) })
            } ?: Resource.Error.emptyResponse()
        } else {
            response.asError()
        }
    }
}
