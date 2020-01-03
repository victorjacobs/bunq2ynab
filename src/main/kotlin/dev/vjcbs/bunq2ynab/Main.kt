package dev.vjcbs.bunq2ynab

import com.sksamuel.hoplite.ConfigLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Path

fun main() = runBlocking {
    val log = logger()
    val config = ConfigLoader().loadConfigOrThrow<Config>(Path.of("bunq2ynab.yaml"))

    val importers = config.importers.map {
        TransactionImporter(it)
    }

    while (true) {
        launch {
            importers.map { syncService ->
                launch {
                    try {
                        syncService.importTransactions()
                    } catch (e: Throwable) {
                        log.error("Error syncing", e)
                    }
                }
            }
        }.join()

        delay(60000)
    }
}
