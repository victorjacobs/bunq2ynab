package dev.vjcbs.bunq2ynab

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val log = logger()

    val syncService = SyncService()

    while (true) {
        try {
            syncService.syncLastOutGoingTransactions()
        } catch (e: Throwable) {
            log.error("Error syncing", e)
        }

        delay(60000)
    }
}
