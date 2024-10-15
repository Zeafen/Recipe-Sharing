package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.response.AuthResult


interface IAuthRepository {

    suspend fun register(login : String, password : String) : AuthResult<String>

    suspend fun logIn(login: String, password: String) : AuthResult<String>

    suspend fun authorize(token : String) : AuthResult<String>
}