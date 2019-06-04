package dev.vjcbs.bunq2ynab

import dev.vjcbs.bunq2ynab.client.BunqClient
import dev.vjcbs.bunq2ynab.client.YnabClient

class SyncService {

    private val log = logger()

    private val ynabClient: YnabClient = YnabClient()

    private val bunqClient: BunqClient = BunqClient()

    suspend fun syncLastOutGoingTransactions() {
        val bunqTransactions = bunqClient.getOutgoingTransactionsForAllBankAccounts()

        log.info("Got ${bunqTransactions.size} transactions from Bunq")

        val importResult = ynabClient.createTransactions(bunqTransactions)

        log.info(
            "${importResult.transactionIds.size} transactions imported, ${importResult.duplicateImportIds.size} " +
                "duplicates"
        )
    }
}
