package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal



class BillingServiceTest {



    private val invoicePending = Invoice(1, 1, Money(BigDecimal.TEN, Currency.EUR), InvoiceStatus.PENDING)
    private val invoiceProgress = invoicePending.copy(status = InvoiceStatus.IN_PROGRESS)
    private val invoicePaid = invoicePending.copy(status = InvoiceStatus.PAID)

    @Test
    fun `mark as paid an Invoice that was charged`() {


        val dal = mockk<AntaeusDal> {
            every { updateInvoiceStatus(1, InvoiceStatus.PAID) } returns invoicePaid
        }

        val customerService = CustomerService(dal = dal)

        val invoiceService = mockk<InvoiceService> {
            every { updateInvoiceStatus(invoicePending.id,InvoiceStatus.PAID) } returns invoicePaid
        }

        val paymentProvider = mockk<PaymentProvider> {
            every { charge(invoicePending) } returns true
        }


        val billingService = BillingService(paymentProvider=paymentProvider,invoiceService=invoiceService,customerService=customerService)


        val updatedInvoice = billingService.processInvoice(invoicePending)

        assertEquals(InvoiceStatus.PAID, updatedInvoice.status)

    }

    @Test
    fun `process All pending invoices and mark them as PAID or FAILED`() {


        val dal = mockk<AntaeusDal> {
            every { updateInvoiceStatus(1, InvoiceStatus.PAID) } returns invoicePaid
        }

        val customerService = CustomerService(dal = dal)

        val invoiceService = mockk<InvoiceService> {
            every { updateInvoiceStatus(invoicePending.id,InvoiceStatus.PAID) } returns invoicePaid
            every { updateInvoiceStatus(invoicePending.id,InvoiceStatus.IN_PROGRESS) } returns invoiceProgress
            every { fetch(invoicePending.id) } returns invoicePaid
            every { fetchByStatus(InvoiceStatus.PENDING) } returns listOf(invoicePending)

        }

        val paymentProvider = mockk<PaymentProvider> {
            every { charge(invoicePending) } returns true
        }

        val billingService = BillingService(paymentProvider=paymentProvider,invoiceService=invoiceService,customerService=customerService)


        billingService.processPaymentByStatus(InvoiceStatus.PENDING)

        assertEquals(InvoiceStatus.PAID, invoiceService.fetch(invoicePending.id).status)


    }
}