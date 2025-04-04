package com.receipts.receipt_sharing.presentation.auth

import com.receipts.receipt_sharing.domain.response.AuthResult

data class  AuthPageState(
    val login : String = "",
    val emailCode : String = "",
    val email : String = "",
    val password : String = "",
    val showPassword : Boolean = false,
    val repeatPassword : String = "",
    val passwordOK : Boolean = false,
    val passwordsMatch : Boolean = false,
    val emailOk : Boolean = false,
    val result : AuthResult<String> = AuthResult.Loading(),
    val infoMessage : String? = null,
)