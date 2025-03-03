package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.domain.response.RecipeResult


interface IAuthRepository {

    suspend fun register(login : String, password : String) : AuthResult<String>

    suspend fun logIn(login: String, password: String) : AuthResult<String>

    suspend fun sendCode(email : String) : RecipeResult<Unit>
    suspend fun updatePassword(request : ChangePasswRequest) : RecipeResult<Unit>

    suspend fun authorize(token : String) : AuthResult<String>
}