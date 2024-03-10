package io.pazzk

import io.pazzk.core.webclient.RestClient
import kotlinx.coroutines.CoroutineScope

/**
 * 치지직의 후원 시스템, 치즈의 후원 알림을 받아올 수 있는 인터페이스(API)
 *
 * @since 1.0
 */
interface Pazzk {
    companion object {
        fun pazzk(donationId: String): Pazzk {
            return PazzkImpl(RestClient(donationId))
        }

        fun pazzk(donationId: String, scope: CoroutineScope): Pazzk {
            return PazzkImpl(RestClient(donationId), scope)
        }
    }

    fun addListener(callback: (response: Any) -> Unit)

    fun connect(useParentScope: Boolean = false)

    fun disconnect()

}