package ru.normal.trans34.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import jakarta.inject.Inject
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger

class TransportApi @Inject constructor(
    private val client: HttpClient
) {
    private val counter = AtomicInteger(1)
    private var sessionId: String? = null

    private companion object {
        private const val BASE_URL = "https://transport.volganet.ru/api/rpc.php"
        private const val TAG = "TransportApi"
    }

    private suspend fun ensureSession() {
        if (sessionId != null) return

        val requestId = counter.getAndIncrement()
        val body = JSONObject(
            mapOf(
                "jsonrpc" to "2.0", "method" to "startSession", "id" to requestId
            )
        )

        val response = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val result = JSONObject(response.bodyAsText()).optJSONObject("result")
        sessionId = result?.getString("sid") ?: error("Не удалось инициализировать сессию: $result")

        Log.i(TAG, "Session started with sid=$sessionId")
    }

    private fun generateSecurityData(method: String, id: Int, sid: String): Pair<String, String> {
        val raw = "$method-$id-$sid"
        val bytes = MessageDigest.getInstance("SHA-1").digest(raw.toByteArray())
        val hash = bytes.joinToString("") { "%02x".format(it) }

        val guid = buildString {
            append(hash.substring(0, 8)).append('-')
            append(hash.substring(8, 12)).append('-')
            append(hash.substring(12, 16)).append('-')
            append(hash.substring(24, 28)).append('-')
            append(hash.substring(28))
        }

        val magic = hash.substring(16, 24)
        return guid to magic
    }

    private suspend fun rpcRequest(method: String, params: JSONObject): JSONArray {
        ensureSession()

        val sid = sessionId ?: return JSONArray()
        val requestId = counter.getAndIncrement()

        val (guid, magic) = generateSecurityData(method, requestId, sid)
        params.put("sid", sid)
        params.put("magic", magic)

        val requestBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", method)
            put("params", params)
            put("id", requestId)
        }

        return try {
            val response = client.post("$BASE_URL?m=$guid") {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            val jsonText = response.bodyAsText()
            val json = JSONObject(jsonText)

            Log.d(TAG, "[$method] id=$requestId guid=$guid -> $jsonText")

            json.optJSONArray("result") ?: JSONArray()
        } catch (e: Exception) {
            Log.e(TAG, "RPC error ($method): ${e.message}", e)
            JSONArray()
        }
    }

    suspend fun getStops(
        maxLatitude: Double, maxLongitude: Double, minLatitude: Double, minLongitude: Double
    ): JSONArray {
        val params = JSONObject(
            mapOf(
                "minlat" to (minLatitude.toString() + "5").toDouble(),
                "maxlat" to (maxLatitude.toString() + "5").toDouble(),
                "minlong" to (minLongitude.toString() + "5").toDouble(),
                "maxlong" to (maxLongitude.toString() + "5").toDouble()
            )
        )
        return rpcRequest("getStopsInRect", params)
    }

    suspend fun getStopArriveList(stopId: Int): JSONArray {
        val params = JSONObject(mapOf("st_id" to stopId))
        return rpcRequest("getStopArrive", params)
    }

    suspend fun getUnitArriveList(unitId: String): JSONArray {
        val params = JSONObject(mapOf("unit_id" to unitId))
        return rpcRequest("getUnitArrive", params)
    }

    suspend fun getUnits(
        maxLatitude: Double, maxLongitude: Double, minLatitude: Double, minLongitude: Double
    ): JSONArray {
        val params = JSONObject(
            mapOf(
                "minlat" to (minLatitude.toString() + "5").toDouble(),
                "maxlat" to (maxLatitude.toString() + "5").toDouble(),
                "minlong" to (minLongitude.toString() + "5").toDouble(),
                "maxlong" to (maxLongitude.toString() + "5").toDouble()
            )
        )
        return rpcRequest("getUnitsInRect", params)
    }
}
