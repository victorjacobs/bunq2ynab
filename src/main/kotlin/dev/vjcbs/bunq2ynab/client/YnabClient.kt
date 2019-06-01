package dev.vjcbs.bunq2ynab.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.vjcbs.bunq2ynab.Configuration
import dev.vjcbs.bunq2ynab.Transaction
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.text.DateFormat

class YnabClient {

    val accountId by lazy {
        runBlocking {
            getAccountIdMarkedAsBunqImport()
        }
    }

    private val baseUrl = "https://api.youneedabudget.com/v1"

    private val httpClient = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }

        install(JsonFeature) {
            serializer = JacksonSerializer {
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                dateFormat = DateFormat.getDateInstance()
                registerModule(JavaTimeModule())
            }
        }

        defaultRequest {
            header("User-Agent", "dev.vjcbs.bunq2ynab/1.0")
            header("Authorization", "Bearer ${Configuration.ynabApiKey}")

            if (method != HttpMethod.Get) {
                contentType(ContentType.Application.Json)
            }
        }
    }

    private fun buildUrl(path: String) = "$baseUrl/$path"

    private suspend fun getAccountsForLastUsedBudget() =
        httpClient.get<YnabApiResponse<AccountsListResponse>>(buildUrl("budgets/last-used/accounts")).data.accounts

    private suspend fun getAccountIdMarkedAsBunqImport() =
        getAccountsForLastUsedBudget().firstOrNull {
            it.note?.contains("#bunqimport") ?: false
        }?.id ?: throw IllegalStateException("No account marked with #bunqimport")

    suspend fun createTransaction(transaction: Transaction) =
        createTransactions(listOf(transaction))

    suspend fun createTransactions(transactions: List<Transaction>) =
        httpClient.post<YnabApiResponse<TransactionsCreateResponse>>(buildUrl("budgets/last-used/transactions")) {
            body = TransactionsCreateRequest(transactions.map {
                YnabTransaction.fromTransaction(it, accountId)
            })
        }.data
}
