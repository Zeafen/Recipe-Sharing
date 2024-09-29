package com.receipts.receipt_sharing.data.response

sealed class RecipeResult<T>(val data : T? = null, val info : String? = null) {
    class Succeed<T>(data: T? = null) : RecipeResult<T>(data)
    class Downloading<T> : RecipeResult<T>()
    class Error<T>(info : String? = null) : RecipeResult<T>(info = info)
}