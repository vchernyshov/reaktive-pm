package dev.garage.rpm.app.common.main.model

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.doOnBeforeComplete
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.single.asCompletable
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.subscribeOn
import dev.garage.rpm.app.common.main.api.ServerApi
import dev.garage.rpm.app.common.main.util.onlyDigits

class AuthModel(
    private val api: ServerApi,
    private val tokenStorage: TokenStorage
) {

    fun isAuth() = tokenStorage.getToken().isNotEmpty()

    fun sendPhone(phone: String): Completable {
        return api.sendPhone(phone.onlyDigits())
            .subscribeOn(ioScheduler)
    }

    fun sendConfirmationCode(phone: String, code: String): Completable {
        return api.sendConfirmationCode(phone.onlyDigits(), code.onlyDigits())
            .subscribeOn(ioScheduler)
            .doOnBeforeSuccess { tokenStorage.saveToken(it.token) }
            .asCompletable()
    }

    fun logout(): Completable {
        return api.logout(tokenStorage.getToken())
            .subscribeOn(ioScheduler)
            .doOnBeforeComplete { tokenStorage.clear() }
    }
}