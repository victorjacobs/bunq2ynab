package dev.vjcbs.bunq2ynab

object Configuration {

    val ynabApiKey = getFromEnvOrThrow("YNAB_API_KEY")

    val bunqApiKey = getFromEnvOrThrow("BUNQ_API_KEY")

    private fun getFromEnvOrThrow(varName: String) =
        System.getenv(varName) ?: throw IllegalStateException("$varName not set")

}
