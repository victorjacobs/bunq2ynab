package dev.vjcbs.bunq2ynab

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val syncService = SyncService()

    while (true) {
        syncService.syncLastOutGoingTransactions()

        delay(60000)
    }
}
