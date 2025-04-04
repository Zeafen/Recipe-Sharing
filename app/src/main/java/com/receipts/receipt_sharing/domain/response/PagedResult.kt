package com.receipts.receipt_sharing.domain.response
import kotlinx.serialization.Serializable
@Serializable
data class PagedResult<T>(
    val result : T,
    val currentPage : Int,
    val totalPages : Int,
    val pageSize : Int
)