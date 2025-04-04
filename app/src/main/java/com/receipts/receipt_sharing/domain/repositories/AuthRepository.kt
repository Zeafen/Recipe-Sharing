package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.AuthResult


interface AuthRepository {

    /**
     * Attempts to register with passed parameters
     * @param login User login
     * @param email User email address
     * @param password User password
     * @return [AuthResult.Error] if register request failed; [AuthResult.Authorized] if register request succeeded
     */
    suspend fun register(login : String, email: String, password : String) : AuthResult<String>

    /**
     * Attempts tp authorize with passed parameters
     * @param login User login or email address
     * @param password User password
     * @return [AuthResult.Error] if register authorize failed; [AuthResult.Authorized] if authorize request succeeded
     */
    suspend fun logIn(login: String, password: String) : AuthResult<String>

    /**
     * Attempts to request confirmation code to be sent to the passed email
     * @param email Email to send code to
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if code has successfully been sent
     */
    suspend fun sendCode(email : String) : ApiResult<Unit>

    /**
     * Attempts to request user password to be changed
     * @param request request body
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if password has successfully been changed
     */
    suspend fun updatePassword(request : ChangePasswRequest) : ApiResult<Unit>

    /**
     * Attempts to check if authorization token is valid
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if token is valid
     */
    suspend fun authorize(token : String) : AuthResult<String>
}