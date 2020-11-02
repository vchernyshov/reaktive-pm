package dev.garage.rpm.app.common.socketIO

import com.badoo.reaktive.observable.doOnBeforeNext
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.socketIO.SocketEvent
import dev.garage.rpm.socketIO.SocketOptions
import dev.garage.rpm.socketIO.socketControl
import dev.garage.rpm.state

class SocketPm : PresentationModel() {

    val updateStatusCommand = state<String>()

    private val socketIOControl = socketControl(
        "https://socketio-chat-h9jt.herokuapp.com",
        SocketOptions(
            queryParams = null,
            transport = SocketOptions.Transport.WEBSOCKET
        )
    ) {
        on(SocketEvent.Connect) {
            updateStatusCommand.accept("connect")
        }

        on(SocketEvent.Connecting) {
            updateStatusCommand.accept("connecting")
        }

        on(SocketEvent.Disconnect) {
            updateStatusCommand.accept("disconnect")
        }

        on(SocketEvent.Error) {
            updateStatusCommand.accept("error $it")
        }

        on(SocketEvent.Reconnect) {
            updateStatusCommand.accept("reconnect")
        }

        on(SocketEvent.ReconnectAttempt) {
            updateStatusCommand.accept("reconnect attempt $it")
        }

        on(SocketEvent.Ping) {
            updateStatusCommand.accept("ping")
        }

        on(SocketEvent.Pong) {
            updateStatusCommand.accept("pong")
        }

        listOf(
            "input",
            "login",
            "new message",
            "user joined",
            "user left",
            "typing",
            "stop typing"
        ).forEach { eventName ->
            on(eventName) { data ->
                updateStatusCommand.accept("$eventName $data")
            }
        }
    }

    val connectAction = action<Unit> {
        this.doOnBeforeNext {
            socketIOControl.connect()
        }
    }

    val disconnectAction = action<Unit> {
        this.doOnBeforeNext {
            socketIOControl.disconnect()
        }
    }

    val login = action<Unit> {
        this.doOnBeforeNext {
            socketIOControl.emit("add user", "mokoSocketIo")
        }
    }
}