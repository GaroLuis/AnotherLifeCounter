package io.github.garoluis.anotherlifecounter.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Serializable
data class ImageUris(
    val small: String? = null,
)

@Serializable
data class Card(
    val name: String = "",
    val flavor_name: String? = null,
    val image_uris: ImageUris? = null
)

@Serializable
data class AutocompleteResponse(
    val data: List<Card> = emptyList()
)

object ScryfallApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun searchCommanders(query: String): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        val url = "https://api.scryfall.com/cards/search?q=name:${query.trim()}+t:legendary+(t:creature+or+t:vehicle)"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "AnotherLifeCounter/1.0")
            .header("Accept", "application/json")
            .build()

        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            Log.d("ScryfallApi", "Query: $query, Status: ${response.code}, Body: $body")
            if (response.isSuccessful) {
                val result = json.decodeFromString<AutocompleteResponse>(body).data.map{it.flavor_name ?: it.name}
                Log.d("ScryfallApi", "Results: $result")
                result
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
