/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.garage.rpm.socketIO

import kotlinx.serialization.SerializationException

actual sealed class SocketEvent<T> : Mapper<T> {

    actual object Connect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
    }

    actual object Connecting : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
    }

    actual object Disconnect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
    }

    actual object Error : SocketEvent<Throwable>() {
        override fun mapper(data: List<*>): Throwable {
            return SerializationException("")
        }
    }

    actual object Message : SocketEvent<Any>() {
        override fun mapper(data: List<*>): Any {
            return Unit
        }
    }

    actual object Reconnect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
    }

    actual object ReconnectAttempt : SocketEvent<Int>() {
        override fun mapper(data: List<*>): Int {
            return 0
        }
    }

    actual object Ping : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
    }

    actual object Pong : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
    }

    private class UnitMapper : Mapper<Unit> {
        override fun mapper(data: List<*>) = Unit
    }
}
