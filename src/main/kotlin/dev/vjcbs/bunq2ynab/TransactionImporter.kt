package dev.vjcbs.bunq2ynab

import dev.vjcbs.bunq2ynab.client.BunqClient
import dev.vjcbs.bunq2ynab.client.YnabClient

class TransactionImporter(
    private val config: ImportConfig
) {
    private val log = logger()

    private val ynab: YnabClient = YnabClient(config.ynab)

    private val bunq: BunqClient = BunqClient(config.bunq)

    suspend fun importTransactions() {
        var bunqTransactions = bunq.getTransactions()

        if (!config.syncAllTransactions) {
            bunqTransactions = bunqTransactions.filter {
                it.amount < 0
            }
        }

        log.info("Got ${bunqTransactions.size} transactions from Bunq")

        val importResult = ynab.createTransactions(bunqTransactions)

        log.info(
            "${importResult.transactionIds.size} transactions imported, ${importResult.duplicateImportIds.size} " +
                "duplicates"
        )
    }
}
