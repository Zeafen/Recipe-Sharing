package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.repositories.AuthRepository
import com.receipts.receipt_sharing.domain.request.AuthRequest
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.AuthResult
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: RecipesAPIService
) : AuthRepository {
    override suspend fun register(login: String, email : String, password: String): AuthResult<String> {
        return try {
            api.signUp(AuthRequest(login, password, email))
            logIn(login, password)
        } catch (e: HttpException) {
            AuthResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.message)
        }
    }

    override suspend fun sendCode(email: String): ApiResult<Unit> {
        return try {
            api.sendCode(email)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message)
        }

    }

    override suspend fun updatePassword(request: ChangePasswRequest): ApiResult<Unit> {
        return try {
            api.updatePassword(request)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message)
        }
    }

    override suspend fun authorize(token: String): AuthResult<String> {
        return try {
            api.authorize(token)
            AuthResult.Authorized(null)
        } catch (e: HttpException) {
            if (e.code() == 401)
                AuthResult.Unauthorized()
            else {
                e.printStackTrace()
                AuthResult.Error(e.response()?.errorBody()?.string() ?: e.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.message)
        }

    }

    override suspend fun logIn(login: String, password: String): AuthResult<String> {
        return try {
            val data = api.signIn(AuthRequest(login, password, ""))
            AuthResult.Authorized(data)
        } catch (e: HttpException) {
            e.printStackTrace()
            AuthResult.Error(e.response()?.errorBody()?.string() ?: e.message())
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Error(e.message)
        }
    }
}