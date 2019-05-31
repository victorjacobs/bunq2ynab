package dev.vjcbs.bunq2ynab

import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.model.generated.`object`.NotificationFilter
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.Payment
import com.bunq.sdk.model.generated.endpoint.User
import java.io.File

class BunqClient {

    private val contextFile = "bunq.conf"

    init {
        login()
    }

    fun login() {
        val apiContext = if (File(contextFile).exists()) {
            ApiContext.restore(contextFile)
        } else {
            ApiContext.create(
                ApiEnvironmentType.PRODUCTION,
                Configuration.bunqApiKey,
                "dev.vjcbs.bunq2ynab"
            )
        }

        apiContext.save(contextFile)

        BunqContext.loadApiContext(apiContext)
    }

    fun getTransactions() =
        Payment.list(1279510).value.map {
            Transaction.fromBunqPayment(it)
        }

    fun getUserInformation() = User.get().value

    fun setupNotificationFilter() {
        val notificationFilters = MonetaryAccount.get(1279510).value.monetaryAccountBank.notificationFilters

        notificationFilters.forEach {
            print(it)
        }

        notificationFilters.add(NotificationFilter().apply {
            notificationDeliveryMethod = "URL"
            notificationTarget = "https://b.vjcbs.dev/callback"
            category = "MUTATION"
        })
    }

}
