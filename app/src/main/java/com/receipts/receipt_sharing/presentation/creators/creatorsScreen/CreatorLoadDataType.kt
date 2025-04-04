package com.receipts.receipt_sharing.presentation.creators.creatorsScreen

sealed interface CreatorLoadDataType{
    data object All : CreatorLoadDataType
    data class Follows(val creatorID: String? = null) : CreatorLoadDataType
    data class Followers(val creatorID : String? = null) : CreatorLoadDataType
}