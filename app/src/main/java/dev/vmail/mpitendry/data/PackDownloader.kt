package dev.vmail.mpitendry.data

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

object PackDownloader {

    private val http = OkHttpClient()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun downloadPack(url: String): PlanningPack {
        val req = Request.Builder().url(url).build()
        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code}: ${resp.message}")
            }
            val body = resp.body?.string() ?: throw IllegalStateException("Réponse vide")
            return json.decodeFromString(PlanningPack.serializer(), body)
        }
    }
}
