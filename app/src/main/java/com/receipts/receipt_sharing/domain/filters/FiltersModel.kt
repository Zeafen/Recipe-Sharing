package com.receipts.receipt_sharing.domain.filters

import kotlinx.serialization.Serializable

@Serializable
data class FiltersModel(
    val tags : List<String>? = null,
    val ingredients : List<String>? = null,
    val timeFrom : Int? = null,
    val timeTo : Int? = null,
)
