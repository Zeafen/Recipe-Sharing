package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.repositories.IAuthRepository
import com.receipts.receipt_sharing.domain.request.AuthRequest
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api : RecipesAPIService
) : IAuthRepository {
    override suspend fun register(login: String, password: String): AuthResult<String> {
        return try {
            api.signUp(AuthRequest(login, password))
            logIn(login, password)
        }
        catch (e : HttpException){
            AuthResult.Error(e.message)
        }
        catch (e : Exception){
            e.printStackTrace()
            AuthResult.Error(e.message)
        }
    }

    override suspend fun authorize(token: String): AuthResult<String> {
        return try {
            api.authorize(token)
            AuthResult.Authorized(null)
        }
        catch (e : HttpException){
            e.printStackTrace()
            AuthResult.Error(e.message)
        }
        catch (e : Exception){
            e.printStackTrace()
            AuthResult.Error(e.message)
        }

    }

    override suspend fun logIn(login: String, password: String): AuthResult<String> {
        return try {
            val data = api.signIn(AuthRequest(login, password))
            AuthResult.Authorized(data)
        }
        catch (e : HttpException){
            e.printStackTrace()
            AuthResult.Error(e.message)
        }
        catch (e : Exception){
            e.printStackTrace()
            AuthResult.Error(e.message)
        }
    }
}