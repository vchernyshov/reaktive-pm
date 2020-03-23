package dev.garage.rpm.app.main.api

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.single.Single

interface ServerApi {
    fun sendPhone(phone: String): Completable
    fun sendConfirmationCode(phone: String, code: String): Single<TokenResponse>
    fun logout(token: String): Completable
}

class WrongConfirmationCode(message: String) : Throwable(message)
class ServerError(message: String) : Throwable(message)

class TokenResponse(val token: String)