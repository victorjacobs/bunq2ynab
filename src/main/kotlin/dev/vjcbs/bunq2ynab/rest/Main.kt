package dev.vjcbs.bunq2ynab.rest

import com.bunq.sdk.model.generated.endpoint.NotificationFilterUrlUser
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.vjcbs.bunq2ynab.client.BunqClient
import dev.vjcbs.bunq2ynab.client.YnabClient
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.apache.commons.net.util.SubnetUtils
import java.text.DateFormat

fun Application.main() {
    install(Compression)
    install(ContentNegotiation) {
        jackson {
            dateFormat = DateFormat.getDateInstance()
            registerModule(JavaTimeModule())
        }
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Error")
            throw cause
        }
    }

    val ynabClient = YnabClient()
    val bunqClient = BunqClient()

    routing {
        get("/test") {
            bunqClient.setupNotificationFilter()

            call.respond(NotificationFilterUrlUser.list().value)
        }

        get("/bunq2ynab-callback") {
            val requestOriginIp = call.request.origin.host
            if (!SubnetUtils("185.40.108.0/22").info.isInRange(requestOriginIp)) {
                log.error("Unauthorized request from $requestOriginIp")
                throw IllegalStateException("Unauthorized")
            }

            val importResult = ynabClient.createTransactions(bunqClient.getOutgoingTransactionsForAllBankAccounts())

            log.info("${importResult.transactionIds.size} imported, ${importResult.duplicateImportIds.size} duplicates")

            call.respond(importResult)
        }
    }
}
