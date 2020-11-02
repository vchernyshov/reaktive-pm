package dev.garage.rpm.socketIO

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

interface SocketHandler {

    fun emit(event: String, data: JsonObject)
    fun emit(event: String, data: JsonArray)
    fun emit(event: String, data: String)
    fun connect()
    fun disconnect()
    fun isConnected(): Boolean
}
