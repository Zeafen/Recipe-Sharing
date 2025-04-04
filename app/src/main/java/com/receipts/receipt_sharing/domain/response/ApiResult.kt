package com.receipts.receipt_sharing.domain.response

sealed class ApiResult<T>(val data : T? = null, val info : String? = null) {
    class Succeed<T>(data: T? = null) : ApiResult<T>(data)
    class Downloading<T> : ApiResult<T>()
    class Error<T>(info : String? = null) : ApiResult<T>(info = info)
}