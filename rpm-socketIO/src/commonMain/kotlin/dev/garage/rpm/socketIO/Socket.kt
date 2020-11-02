/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.socketIO

expect class Socket(
    endpoint: String,
    config: SocketOptions? = null,
    build: SocketBuilder.() -> Unit
) : SocketHandler
