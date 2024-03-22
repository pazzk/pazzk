package io.pazzk

import io.pazzk.core.webclient.RestClient
import io.pazzk.utils.Context
import kotlinx.coroutines.CoroutineScope

/**
 * Chzzk sponsorship system, an interface (API) to receive sponsorship
 * notifications from Cheese.
 *
 * When using, be sure to call it in the following order:
 * > addListener -> connect -> disconnect
 *
 * Do not set connect first. well you can, but not really recommend
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

    /**
     * Add Listener(actually subscriber) that read about chzzk donation server
     *
     * @param callback define non-returnable implementation with using context
     */
    fun addListener(callback: (response: Context) -> Unit)

    /**
     * Connect to chzzk donation server.
     *
     * @param useParentScope when is true, use scope from constructor
     * when is false, use default scope
     * default will be false
     * @see CoroutineScope
     */
    fun connect(useParentScope: Boolean = false)

    /**
     * Disconnect to chzzk donation server
     */
    fun disconnect()

}