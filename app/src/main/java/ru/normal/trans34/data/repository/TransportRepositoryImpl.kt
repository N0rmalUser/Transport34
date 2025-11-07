package ru.normal.trans34.data.repository

import android.util.Log
import org.json.JSONArray
import ru.normal.trans34.data.remote.TransportApi
import ru.normal.trans34.domain.entity.Route
import ru.normal.trans34.domain.repository.TransportRepository
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.StopPoint
import ru.normal.trans34.domain.entity.UnitPoint

class TransportRepositoryImpl(
    private val api: TransportApi
) : TransportRepository {

    override suspend fun getUnits(mapBorders: MapBorders): List<UnitPoint> {
        val json: JSONArray =  api.getUnits(
            mapBorders.maxLatitude,
            mapBorders.maxLongitude,
            mapBorders.minLatitude,
            mapBorders.minLongitude
        )

        Log.e("TransportVolganet", "mapStops: $json")
        return List(json.length()) { i ->
            val obj = json.getJSONObject(i)
            UnitPoint(
                id = obj.optString("mr_id", "N/A"),
                routeNumber = obj.optString("mr_num", "N/A"),
                destinationRu = obj.optString("rl_laststation_title", "N/A"),
                destinationEn = obj.optString("rl_laststation_title_en", "N/A"),
                transportType = obj.optInt("tt_id", 0),
                speed = obj.optString("u_speed", "N/A"),
                latitude = obj.optString("u_lat", "0.0").toDouble(),
                longitude = obj.optString("u_long", "0.0").toDouble(),
                azimuth = obj.optString("u_course", "N/A"),
                systemTime = obj.optString("tc_systime", "N/A")
            )
        }
    }

    override suspend fun getStops(mapBorders: MapBorders): List<StopPoint> {
        val json: JSONArray =  api.getStops(
            mapBorders.maxLatitude,
            mapBorders.maxLongitude,
            mapBorders.minLatitude,
            mapBorders.minLongitude
        )

        Log.e("TransportVolganet", "mapStops: $json")
        return List(json.length()) { i ->
            val obj = json.getJSONObject(i)
            StopPoint(
                id = obj.optInt("st_id", 0),
                latitude = obj.optString("st_lat", "0.0").toDouble(),
                longitude = obj.optString("st_long", "0.0").toDouble(),
                destinationEn = obj.optString("st_title_en", "N/A"),
                destinationRu = obj.optString("st_title", "N/A")
            )
        }
    }

    override suspend fun getStopArriveList(stopId: Int): List<Route> {
        val json: JSONArray = api.getStopArriveList(stopId)

        Log.e("TransportVolganet", "stopId=$stopId: $json")
        return List(json.length()) { i ->
            val obj = json.getJSONObject(i)
            Route(
                id = obj.optInt("mr_id", 0),
                routeNumber = obj.optString("mr_num", "N/A"),
                destinationRu = obj.optString("laststation_title", "N/A"),
                destinationEn = obj.optString("laststation_title_en", "N/A"),
                arrivalTime = obj.optString("tc_arrivetime", "N/A"),
                systemTime = obj.optString("tc_systime", "N/A"),
                transportType = obj.optInt("tt_id", 0)
            )
        }
    }
}