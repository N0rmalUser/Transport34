package ru.normal.trans34.data.repository

import android.util.Log
import org.json.JSONArray
import ru.normal.trans34.data.remote.TransportApi
import ru.normal.trans34.domain.entity.Route
import ru.normal.trans34.domain.repository.TransportRepository
import ru.normal.trans34.domain.entity.MapBorders
import ru.normal.trans34.domain.entity.StopPoint

class TransportRepositoryImpl(
    private val api: TransportApi
) : TransportRepository {

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