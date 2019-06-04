package dev.vjcbs.bunq2ynab.client

import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.model.generated.`object`.NotificationFilterUrl
import com.bunq.sdk.model.generated.endpoint.MonetaryAccountBank
import com.bunq.sdk.model.generated.endpoint.NotificationFilterUrlUser
import com.bunq.sdk.model.generated.endpoint.Payment
import dev.vjcbs.bunq2ynab.Configuration
import dev.vjcbs.bunq2ynab.Transaction
import dev.vjcbs.bunq2ynab.logger
import java.io.File
import java.io.FileNotFoundException

class BunqClient {

    val log = logger()

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

        try {
            apiContext.save(contextFile)
        } catch (e: FileNotFoundException) {
            log.warn("Unable to save API context: ${e.message}")
        }

        BunqContext.loadApiContext(apiContext)
    }

    fun getOutgoingTransactionsForAllBankAccounts() =
        MonetaryAccountBank.list().value.flatMap { bankAccount ->
            Payment.list(bankAccount.id).value.filter { payment ->
                payment.type != "SAVINGS" && payment.amount.value.toDouble() < 0
            }.map {
                Transaction.fromBunqPayment(it)
            }
        }

    fun setupNotificationFilter() {
        // Currently broken because of https://github.com/bunq/sdk_java/issues/117
        val notificationFilters = NotificationFilterUrlUser.list().value.flatMap { notificationFilterUrlUser ->
            notificationFilterUrlUser.notificationFilters?.filter { notificationFilterUrl ->
                !notificationFilterUrl.notificationTarget.contains("bunq2ynab")
            } ?: listOf()
        } + NotificationFilterUrl().apply {
            category = "MUTATION"
            notificationTarget = "https://${Configuration.domainName}/bunq2ynab-callback"
        }

        NotificationFilterUrlUser.create(notificationFilters)
//        val url = "user/${BunqContext.getUserContext().userId}/notification-filter-url"
//
//        val gson = BunqGsonBuilder.buildDefault().create()
//
//        val data = mapOf("notification_filters" to listOf(NotificationFilterUrl().apply {
//            category = "MUTATION"
//            notificationTarget = "https://${Configuration.domainName}/bunq2ynab-callback"
//        }))
//
// //        val data = mapOf("notification_filters" to listOf<NotificationFilterUrl>())
//
//        val serializedData = gson.toJson(data).toByteArray()
//
//        val client = ApiClient(BunqContext.getApiContext())
//
//        val createResult = String(client.post(url, serializedData, mapOf()).bodyBytes)
//        println(createResult)
//        val listResult = String(client.get(url, mapOf(), mapOf()).bodyBytes)
//        println(listResult)
    }
}
