package dev.vjcbs.bunq2ynab.client

import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.exception.BunqException
import com.bunq.sdk.model.generated.endpoint.Payment
import dev.vjcbs.bunq2ynab.BunqConfig
import dev.vjcbs.bunq2ynab.Transaction
import dev.vjcbs.bunq2ynab.logger
import org.apache.commons.codec.digest.DigestUtils
import java.io.File

class BunqClient(
    private val config: BunqConfig
) {
    private val log = logger()

    private val contextFile = "bunq${DigestUtils.sha1Hex(config.apiKey)}.conf"

    init {
        login()
    }

    fun login() {
        val apiContext = if (File(contextFile).exists()) {
            ApiContext.restore(contextFile)
        } else {
            ApiContext.create(
                ApiEnvironmentType.PRODUCTION,
                config.apiKey,
                "dev.vjcbs.bunq2ynab"
            )
        }

        try {
            apiContext.save(contextFile)
        } catch (e: BunqException) {
            log.warn("Unable to save API context: ${e.message}")
        }

        BunqContext.loadApiContext(apiContext)
    }

    fun getTransactions() =
        Payment.list(config.accountId).value.filter { payment ->
            payment.type != "SAVINGS"
        }.map {
            Transaction.fromBunqPayment(it)
        }
}
