package dev.vjcbs.bunq2ynab

import com.bunq.sdk.model.generated.endpoint.Payment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Transaction(
    val id: Long,
    val date: LocalDate,
    val amount: Double,
    val payeeName: String,
    val description: String?
) {
    companion object {
        private val bunqDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        fun fromBunqPayment(payment: Payment) =
            Transaction(
                id = payment.id.toLong(),
                date = LocalDate.parse(payment.created.split(" ")[0], bunqDateTimeFormatter),
                amount = payment.amount.value.toDouble(),
                payeeName = payment.counterpartyAlias.displayName,
                description = if (payment.description.trim().isEmpty()) {
                    null
                } else {
                    payment.description.trim()
                }
            )
    }
}
