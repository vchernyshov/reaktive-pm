/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.socketIO

import io.socket.client.IO
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Field
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.util.*

actual class Socket actual constructor(
    endpoint: String,
    config: SocketOptions?,
    build: SocketBuilder.() -> Unit
) : SocketHandler {
    private val socketIo: io.socket.client.Socket

    init {
        addURLProtocols()

        socketIo = IO.socket(endpoint, IO.Options().apply {
            transports = config?.transport?.let {
                when (it) {
                    SocketOptions.Transport.DEFAULT -> return@let null
                    SocketOptions.Transport.WEBSOCKET -> return@let arrayOf(WebSocket.NAME)
                    SocketOptions.Transport.POLLING -> return@let arrayOf(Polling.NAME)
                }
            }
            query = config?.queryParams?.run {
                if (size == 0) return@run null

                var result = ""
                forEach { (key, value) ->
                    result += "$key=$value"
                }

                result
            }
        })

        object : SocketBuilder {
            override fun on(event: String, action: Socket.(message: String) -> Unit) {
                socketIo.on(event) {
                    this@Socket.action(rawDataToString(it))
                }
            }

            override fun <T> on(socketEvent: SocketEvent<T>, action: Socket.(data: T) -> Unit) {
                socketEvent.socketIoEvents.forEach { event ->
                    socketIo.on(event) { data ->
                        this@Socket.action(socketEvent.mapper(data))
                    }
                }
            }
        }.build()
    }

    override fun emit(event: String, data: JsonObject) {
        socketIo.emit(event, JSONObject(data.toString()))
    }

    override fun emit(event: String, data: JsonArray) {
        socketIo.emit(event, JSONArray(data.toString()))
    }

    override fun emit(event: String, data: String) {
        socketIo.emit(event, data)
    }

    override fun connect() {
        socketIo.connect()
    }

    override fun disconnect() {
        socketIo.disconnect()
    }

    override fun isConnected(): Boolean {
        return socketIo.connected()
    }

    private fun addURLProtocols() {
        @Suppress("TooGenericExceptionCaught")
        try {
            val field: Field = URL::class.java.getDeclaredField("streamHandlers")
            field.isAccessible = true
            val handler: URLStreamHandler = object : URLStreamHandler() {
                override fun openConnection(u: URL?): URLConnection {
                    return object : URLConnection(u) {
                        @Suppress("EmptyFunctionBlock")
                        override fun connect() {
                        }
                    }
                }
            }
            val handlers: Hashtable<String, URLStreamHandler> =
                field.get(null) as Hashtable<String, URLStreamHandler>
            handlers["wss"] = handler
            handlers["ws"] = handler
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private companion object {
        fun rawDataToString(data: Array<out Any>): String {
            return data.last().toString()
        }
    }
}
