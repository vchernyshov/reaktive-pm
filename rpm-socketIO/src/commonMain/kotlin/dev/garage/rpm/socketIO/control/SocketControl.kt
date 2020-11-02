package dev.garage.rpm.socketIO

import dev.garage.rpm.PresentationModel
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class SocketControl(
    private val socket: Socket
) : PresentationModel(), SocketHandler {

    override fun emit(event: String, data: JsonObject) {
        socket.emit(event, data)
    }

    override fun emit(event: String, data: JsonArray) {
        socket.emit(event, data)
    }

    override fun emit(event: String, data: String) {
        socket.emit(event, data)
    }

    override fun connect() {
        socket.connect()
    }

    override fun disconnect() {
        socket.disconnect()
    }

    override fun isConnected(): Boolean {
        return socket.isConnected()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (socket.isConnected()) {
            socket.disconnect()
        }
    }
}

fun PresentationModel.socketControl(
    endpoint: String,
    config: SocketOptions?,
    builder: SocketBuilder.() -> Unit
): SocketControl =
    SocketControl(Socket(endpoint, config, builder)).apply {
        attachToParent(this@socketControl)
    }
