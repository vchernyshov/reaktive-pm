package dev.garage.rpm.map.command

import dev.garage.rpm.permissions.PermissionResult

typealias PermissionResultListener = (PermissionResult) -> Unit

sealed class CommandType {

    object NoRequiredPermission : CommandType()
    data class RequiredPermission(
        val permissionResultListener: PermissionResultListener? = null,
        val permissionRequiredType: PermissionRequiredType = PermissionRequiredType.NO_MANDATORY,
        var permissionResultType: PermissionResult.Type? = null
    ) : CommandType()
}
