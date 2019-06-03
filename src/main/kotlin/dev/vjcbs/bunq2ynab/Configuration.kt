package dev.vjcbs.bunq2ynab

object Configuration {

    val ynabApiKey = getFromEnvOrThrow("YNAB_API_KEY")

    val bunqApiKey = getFromEnvOrThrow("BUNQ_API_KEY")

    val domainName = getFromEnv("DOMAIN_NAME") ?: "test-url"

    private fun getFromEnv(varName: String): String? = System.getenv(varName)

    private fun getFromEnvOrThrow(varName: String) =
        getFromEnv(varName) ?: throw IllegalStateException("$varName not set")

}
