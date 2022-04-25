package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

const val MAX_RETRIES = 3

class BillingService(private val paymentProvider: PaymentProvider,private val invoiceService: InvoiceService, private val customerService: CustomerService) {



    fun processPaymentByStatus(invoiceStatus: InvoiceStatus) {
        println("PROCESS payment by : ${invoiceStatus.toString()}")

        val listInvoices= invoiceService.fetchByStatus(invoiceStatus)

        listInvoices.forEach { invoice ->
            for (i in 0..MAX_RETRIES) {
                try {
                    invoiceService.updateInvoiceStatus(invoice.id,InvoiceStatus.IN_PROGRESS)
                    processInvoice(invoice)
                    break;
                } catch (e: NetworkException) {
                    //network exception is the only TRANSIENT error in this exercice
                    println("error invoiceID: ${invoice.id} error message : ${e.message}")
                    Thread.sleep(1000)
                    if (i == MAX_RETRIES) {
                        invoiceService.updateInvoiceStatus(invoice.id,InvoiceStatus.ERROR_NETWORK)
                    }
                }catch (e: CurrencyMismatchException) {
                    println("error invoiceID: ${invoice.id} error message : ${e.message}")
                    updateCurrencyInvoice(invoice)
                    if (i == MAX_RETRIES) {
                        invoiceService.updateInvoiceStatus(invoice.id,InvoiceStatus.ERROR_CURRENCY)
                    }
                }
                catch (e: Exception) {
                    println("error invoiceID: ${invoice.id} error message : ${e.message}")
                    invoiceService.updateInvoiceStatus(invoice.id,InvoiceStatus.ERROR_NOT_FOUND)
                    break;

                }

            }
        }

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

    private fun updateCurrencyInvoice(invoice: Invoice) {

        val customer = customerService.fetch(invoice.customerId)
        invoiceService.updateInvoiceCurrency(invoice.id,customer.currency)
    }

}
