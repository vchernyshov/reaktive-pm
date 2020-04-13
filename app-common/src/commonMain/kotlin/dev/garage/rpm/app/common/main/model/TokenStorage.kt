package dev.garage.rpm.app.common.main.model

import com.badoo.reaktive.utils.atomic.AtomicReference

class TokenStorage {

    private var tokenRef = AtomicReference("")

    fun saveToken(token: String) {
        tokenRef.value = token
    }

    fun getToken(): String {
        return tokenRef.value
    }

    fun clear() {
        tokenRef.value = ""
    }
}