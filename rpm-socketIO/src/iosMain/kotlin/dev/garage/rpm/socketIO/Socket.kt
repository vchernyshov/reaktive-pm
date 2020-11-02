/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.socketIO

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

actual class Socket actual constructor(
    endpoint: String,
    config: SocketOptions?,
    build: SocketBuilder.() -> Unit
) : SocketHandler{

    init {
    }

    override fun emit(event: String, data: JsonObject) {
    }

    override fun emit(event: String, data: JsonArray) {
    }

    override fun emit(event: String, data: String) {
    }

    override fun connect() {
    }

    override fun disconnect() {
    }

    override fun isConnected(): Boolean {
        return false
    }
}
