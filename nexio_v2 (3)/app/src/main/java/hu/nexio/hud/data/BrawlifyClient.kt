package hu.nexio.hud.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Brawlify (brawlapi.com) nyilvános, kulcs nélküli API-ja: brawlerek, pályák, játékmódok, események.
 * FIGYELEM: ez játékadatbázis, NEM statisztika (nincs win rate / matchup).
 * Modulba zárva, hogy később könnyen cserélhető legyen.
 */
object BrawlifyClient {
    private const val BASE = "https://api.brawlapi.com/v1"

    suspend fun get(path: String): JSONObject = withContext(Dispatchers.IO) {
        val c = URL("$BASE/$path").openConnection() as HttpURLConnection
        try {
            c.connectTimeout = 8000
            c.readTimeout = 12000
            c.setRequestProperty("Accept", "application/json")
            c.setRequestProperty("User-Agent", "NexioHud/2.1")
            val code = c.responseCode
            if (code !in 200..299) throw IOException("HTTP $code ($path)")
            JSONObject(c.inputStream.bufferedReader().use { it.readText() })
        } finally {
            c.disconnect()
        }
    }

    /** Az API-k többsége {"list":[...]} alakban válaszol. */
    fun JSONObject.items(key: String = "list"): JSONArray = optJSONArray(key) ?: JSONArray()
}
