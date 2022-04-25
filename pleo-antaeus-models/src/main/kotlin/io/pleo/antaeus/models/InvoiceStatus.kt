package io.pleo.antaeus.models

enum class InvoiceStatus {
    PENDING,
    PAID,
    FAILED,
    IN_PROGRESS,
    ERROR_NETWORK,
    ERROR_NOT_FOUND,
    ERROR_CURRENCY

}