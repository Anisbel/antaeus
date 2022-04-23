package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(private val paymentProvider: PaymentProvider,private val invoiceService: InvoiceService, private val customerService: CustomerService)  {

    fun processPaymentByStatus(pending: InvoiceStatus) {
        //TODO
    }

    fun processInvoice(invoice: Invoice): Invoice {
        val status = paymentProvider.charge(invoice);
        val invoiceStatus = getInvoiceStatus(status)
        return invoiceService.updateInvoiceStatus(invoice.id,invoiceStatus)

    }
    private fun getInvoiceStatus(status: Boolean): InvoiceStatus {
        return if(status) InvoiceStatus.PAID
        else InvoiceStatus.FAILED
    }

}
