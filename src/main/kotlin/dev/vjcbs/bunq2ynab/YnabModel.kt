package dev.vjcbs.bunq2ynab

import java.time.LocalDate

// API structure
data class YnabApiResponse<E>(
    val data: E
)

data class AccountsResponse(
    val accounts: List<YnabAccount>
)

data class TransactionsRequest(
    val transactions: List<YnabTransaction>
)

// Models
data class YnabAccount(
    val id: String,
    val name: String,
    val type: String,
    val note: String?
)

data class YnabTransaction(
    val accountId: String,
    val date: LocalDate,
    val amount: Int,
    val payeeName: String,
    val memo: String?,
    val importId: String,
    val approved: Boolean = false,
    val cleared: String = "cleared"
) {
    companion object {
        fun fromTransaction(transaction: Transaction, accountId: String) =
            YnabTransaction(
                accountId = accountId,
                date = transaction.date,
                amount = (transaction.amount * 1000).toInt(),
                payeeName = transaction.payeeName,
                importId = "BUNQ:${transaction.id}",
                memo = transaction.description
            )
    }
}
