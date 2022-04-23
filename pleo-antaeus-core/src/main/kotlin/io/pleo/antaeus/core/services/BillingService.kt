package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(
    private val paymentProvider: PaymentProvider
) {
    fun processPaymentByStatus(pending: InvoiceStatus) {

    }
// TODO - Add code e.g. here
}
