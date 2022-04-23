package io.pleo.antaeus.core.Scheduler;
import io.pleo.antaeus.core.*;
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.InvoiceStatus
import kotlin.Unit;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kotlin.concurrent.*;


class Schedule(private val billingService: BillingService) {

    fun run() {

            val fixedRateTimer = fixedRateTimer(name = "billing-timer",
                    initialDelay = 100, period = TimeUnit.DAYS.toMillis(1)) {
            runSchedule()
        }
    }

    private fun runSchedule() {
        val now = Calendar.getInstance()
        val nowDayOfTheMonth = now[Calendar.DAY_OF_MONTH]
        if(nowDayOfTheMonth==1){
            billingService.processPaymentByStatus(InvoiceStatus.PENDING)
        }else
        { //we can be more granular regarding business logic, but for now we process failed payment(not sufficent fund,etc.) the following days
            billingService.processPaymentByStatus(InvoiceStatus.FAILED)
        }
    }

}
