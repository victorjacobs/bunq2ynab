package dev.vjcbs.bunq2ynab

data class Config(
    val importers: List<ImportConfig>
)

data class YnabConfig(
    val apiKey: String,
    val budgetId: String
)

data class BunqConfig(
    val apiKey: String,
    val accountId: Int
)

data class ImportConfig(
    val ynab: YnabConfig,
    val bunq: BunqConfig,
    val syncAllTransactions: Boolean = false
)
