package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.creators.EmailConfirmRequest
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.response.RecipeResult

interface ICreatorsRepository {

    suspend fun getCreators(token : String) : RecipeResult<List<CreatorRequest>>

    suspend fun getCreatorsByName(token : String, nickname : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getCreatorById(token : String, creatorId : String) : RecipeResult<CreatorRequest>

    suspend fun getFollows(token : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getFollowersCount(token : String) : RecipeResult<Long>
    suspend fun getFollowsByName(token : String, name : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getFollowers(token : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getCreatorFollowers(token : String, creatorID: String) : RecipeResult<List<CreatorRequest>>
    suspend fun getCreatorFollowersCount(token : String, creatorID : String) : RecipeResult<Long>
    suspend fun getCreatorFollowsCount(token : String, creatorID : String) : RecipeResult<Long>
    suspend fun getCreatorRecipesCount(token : String, creatorID : String) : RecipeResult<Long>
    suspend fun addToFollows(token : String, creatorID : String) : RecipeResult<Unit>
    suspend fun removeFromFollows(token : String, creatorID : String) : RecipeResult<Unit>
    suspend fun doesFollow(token : String, creatorID : String) : RecipeResult<Boolean>

    //Emails
    suspend fun setEmail(token : String, email : String) : RecipeResult<Unit>
    suspend fun setEmailGetCode(token : String, email : String) : RecipeResult<Unit>
    suspend fun getCode(token : String) : RecipeResult<Unit>
    suspend fun confirmEmail(token : String, request : EmailConfirmRequest) : RecipeResult<Unit>


    suspend fun updateCreator(token : String, request : ProfileRequest) : RecipeResult<Unit>
    suspend fun updatePassword(token : String, request : ChangePasswRequest) : RecipeResult<Unit>
    suspend fun getUserInfo(token : String) : RecipeResult<ProfileRequest>
}