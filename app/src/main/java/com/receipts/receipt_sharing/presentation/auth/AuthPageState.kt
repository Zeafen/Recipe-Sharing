package com.receipts.receipt_sharing.presentation.auth

import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.presentation.ValidationInfo

data class  AuthPageState(
    val login : String = "",
    val emailCode : String = "",
    val email : String = "",
    val password : String = "",
    val showPassword : Boolean = false,
    val repeatPassword : String = "",
    val passwordValidation : ValidationInfo = ValidationInfo(),
    val passwordsMatch : Boolean = false,
    val emailOk : Boolean = false,
    val result : AuthResult<String> = AuthResult.Loading(),
    val infoMessage : String? = null,
)