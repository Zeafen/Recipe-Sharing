package com.receipts.receipt_sharing.data.response

sealed class AuthResult<T>(val data : T? = null, val info: String? = null) {
    class Authorized<T>(data : T?) : AuthResult<T>(data)
    class Unauthorized<T> : AuthResult<T>()
    class Loading<T> : AuthResult<T>()
    class Error<T>(info : String? = null) : AuthResult<T>(info = info)
}