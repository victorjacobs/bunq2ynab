package dev.vjcbs.bunq2ynab.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.vjcbs.bunq2ynab.Transaction
import dev.vjcbs.bunq2ynab.YnabConfig
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.text.DateFormat

class YnabClient(
    private val config: YnabConfig
) {
    private val accountId by lazy {
        runBlocking {
            getAccountIdMarkedAsBunqImport()
        }
    }

    private val baseUrl = "https://api.youneedabudget.com/v1"

    private val httpClient = HttpClient {
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
            header("Authorization", "Bearer ${config.apiKey}")

            if (method != HttpMethod.Get) {
                contentType(ContentType.Application.Json)
            }
        }
    }

    private fun buildUrl(path: String) = "$baseUrl/$path"

    private suspend fun getAccountsForBudget() =
        httpClient.get<YnabApiResponse<AccountsListResponse>>(buildUrl("budgets/${config.budgetId}/accounts")).data.accounts

    private suspend fun getAccountIdMarkedAsBunqImport() =
        getAccountsForBudget().firstOrNull {
            it.note?.contains("#bunqimport") ?: false
        }?.id ?: throw IllegalStateException("No account marked with #bunqimport")

    suspend fun createTransactions(transactions: List<Transaction>) =
        httpClient.post<YnabApiResponse<TransactionsCreateResponse>>(buildUrl("budgets/${config.budgetId}/transactions")) {
            body = TransactionsCreateRequest(transactions.map {
                YnabTransaction.fromTransaction(it, accountId)
            })
        }.data
}
