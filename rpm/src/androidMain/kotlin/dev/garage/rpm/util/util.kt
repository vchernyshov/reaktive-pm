package dev.garage.rpm.util

import java.util.*

actual fun generateRandomUUID(): String = UUID.randomUUID().toString()
actual fun currentTimeMillis(): Long = System.currentTimeMillis()