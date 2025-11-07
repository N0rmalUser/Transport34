package ru.normal.trans34.data.remote

import android.util.Log
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import jakarta.inject.Inject
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

class TransportApi @Inject constructor(
    private val client: HttpClient
) {
    private var counter = 1
    private var sid: String? = null
    private var guid: String? = null
    private var magic: String? = null
    private var method: String? = null

    private suspend fun startSession() {
        val body = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "startSession")
            put("id", counter)
        }

        val response = client.post("https://transport.volganet.ru/api/rpc.php") {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }

        val result = JSONObject(response.bodyAsText()).getJSONObject("result")
        sid = result.getString("sid")
        counter++
    }

    private fun sha() {
        val sumStr = "$method-$counter-$sid"
        val bytes = MessageDigest.getInstance("SHA-1").digest(sumStr.toByteArray())
        val shaStr = bytes.joinToString("") { "%02x".format(it) }
        guid = "${shaStr.substring(0, 8)}-${shaStr.substring(8, 12)}-" +
                "${shaStr.substring(12, 16)}-${shaStr.substring(24, 28)}-${shaStr.substring(28)}"
        magic = shaStr.substring(16, 24)
    }

    
    suspend fun getStops(
        maxLatitude: Double,
        maxLongitude: Double,
        minLatitude: Double,
        minLongitude: Double
    ): JSONArray {
        if (sid == null) startSession()
        method = "getStopsInRect"
        sha()

        val params = JSONObject().apply {
            put("sid", sid)
            put("minlat", (minLatitude.toString() + "5").toDouble())
            put("maxlat", (maxLatitude.toString() + "5").toDouble())
            put("minlong", (minLongitude.toString() + "5").toDouble())
            put("maxlong", (maxLongitude.toString() + "5").toDouble())
            put("magic", magic)
        }

        val requestBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", method)
            put("params", params)
            put("id", counter)
        }
        Log.w("getStops", requestBody.toString())
        try {
            val response = client.post("https://transport.volganet.ru/api/rpc.php?m=$guid") {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }
            counter ++
            val json =  response.bodyAsText()
            Log.e("getStops", json)
            return JSONObject(json).getJSONArray("result")
        } catch (e: Exception) {
            Log.e("TransportVolganet", "Ошибка при выполнении getStopArriveList: $e")
            return JSONArray()
        }
    }

    suspend fun getStopArriveList(stopId: Int): JSONArray {
        if (sid == null) startSession()
        method = "getStopArrive"
        sha()

        val params = JSONObject().apply {
            put("st_id", stopId)
            put("sid", sid)
            put("magic", magic)
        }

        val requestBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", method)
            put("params", params)
            put("id", counter)
        }
        Log.w("getStopArriveList", requestBody.toString())

        try {
            val response = client.post("https://transport.volganet.ru/api/rpc.php?m=$guid") {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }
            counter ++
            val json =  response.bodyAsText()
            Log.e("getStopArriveList", json)
            return JSONObject(json).getJSONArray("result")
        } catch (e: Exception) {
            Log.e("TransportVolganet", "Ошибка при выполнении getStopArriveList: $e")
            return JSONArray()
        }
    }
}
