package com.receipts.receipt_sharing.ui.auth

import com.receipts.receipt_sharing.domain.response.AuthResult

data class AuthPageState(
    val login : String = "",
    val password : String = "",
    val repeatPassword : String = "",
    val result : AuthResult<String> = AuthResult.Loading()
)
