package com.receipts.receipt_sharing.domain.reviews

data class ReviewModel(
    val id : String,
    val userName : String,
    val userImageUrl : String,
    val text : String,
    val rating : Int,
)