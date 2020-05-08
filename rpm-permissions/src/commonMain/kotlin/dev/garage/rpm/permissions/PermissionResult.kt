package dev.garage.rpm.permissions

data class PermissionResult(
    val permission: Permission,
    val type: Type
) {
    val isGranted: Boolean = type == Type.GRANTED

    enum class Type {
        GRANTED,
        DENIED,
        DENIED_ALWAYS
    }
}