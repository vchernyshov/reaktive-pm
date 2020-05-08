package dev.garage.rpm.permissions

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.firstOrComplete
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.action
import dev.garage.rpm.command

class PermissionControl internal constructor(
    private val permission: Permission
) : PresentationModel() {

    internal val check = command<Permission>()
    internal val request = command<Permission>()
    internal val result = action<PermissionResult>()

    fun check(): Maybe<PermissionResult> =
        result.observable()
            .doOnBeforeSubscribe { check.accept(permission) }
            .firstOrComplete()

    fun request(): Maybe<PermissionResult> =
        result.observable()
            .doOnBeforeSubscribe { request.accept(permission) }
            .firstOrComplete()

    fun checkAndRequest(): Maybe<PermissionResult> =
        check().flatMap { result ->
            if (result.type == PermissionResult.Type.GRANTED) maybeOf(result)
            else request()
        }
}

fun PresentationModel.permissionControl(permission: Permission): PermissionControl =
    PermissionControl(permission).apply {
        attachToParent(this@permissionControl)
    }