package com.receipts.receipt_sharing.presentation.auth

sealed interface AuthEvent {
    data class SetLogin(val login: String) : AuthEvent
    data class SetEmail(val email: String) : AuthEvent
    data class SetEmailCode(val emailCode: String) : AuthEvent
    data class SetPassword(val password: String) : AuthEvent
    data class SetRepeatPassword(val password: String) : AuthEvent
    data class SetShowPassword(val showPassword: Boolean) : AuthEvent
    data object ConfirmLogin : AuthEvent
    data object ConfirmRegister : AuthEvent
    data object Authorize : AuthEvent
    data object SendCode : AuthEvent
    data object ResetPassword : AuthEvent
    data object ClearData : AuthEvent
    data object ClearMessage : AuthEvent
}