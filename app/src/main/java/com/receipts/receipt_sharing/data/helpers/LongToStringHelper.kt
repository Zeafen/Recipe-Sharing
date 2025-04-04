package com.receipts.receipt_sharing.data.helpers

import java.util.Locale

/**
 * Converts Long value to an Alphanumeric string
 */
fun Long.toAmountString(): String {
    return when (this.toString().length) {
        in 1..3 -> this.toString()
        in 4..6 -> "${this.div(1000)},${
            String.format(
                Locale.getDefault(),
                "%.2f",
                this.mod(1000).toFloat() / 1000f
            ).takeLast(2)
        } K"

        in 7..9 -> {
            "${this.toString().dropLast(6)},${
                String.format(
                    Locale.getDefault(),
                    "%.2f",
                    this.toString().dropLast(3).takeLast(3).toFloat() / 1000f
                ).takeLast(2)
            } M"
        }

        in 10..12 -> "${this.toString().dropLast(9)},${
            String.format(
                Locale.getDefault(),
                "%.2f",
                this.toString().dropLast(6).takeLast(3).toFloat() / 1000f
            ).takeLast(2)
        } B"

        else -> "> 100 B"
    }
}