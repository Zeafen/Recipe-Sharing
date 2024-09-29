package com.receipts.receipt_sharing.data.repositories

import com.receipts.receipt_sharing.data.CreatorRequest
import com.receipts.receipt_sharing.data.response.RecipeResult

interface CreatorsRepository {

    suspend fun getCreators(token : String) : RecipeResult<List<CreatorRequest>>

    suspend fun getCreatorsByName(token : String, nickname : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getCreatorById(token : String, creatorId : String) : RecipeResult<CreatorRequest>

    suspend fun getFollows(token : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getFollowsByName(token : String, name : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getFollowers(token : String) : RecipeResult<List<CreatorRequest>>
    suspend fun getCreatorFollowers(token : String, creatorID: String) : RecipeResult<List<CreatorRequest>>

    suspend fun addToFollows(token : String, creatorID : String) : RecipeResult<Unit>
    suspend fun removeFromFollows(token : String, creatorID : String) : RecipeResult<Unit>
    suspend fun doesFollow(token : String, creatorID : String) : RecipeResult<Boolean>


    suspend fun updateCreator(token : String, request : CreatorRequest) : RecipeResult<Unit>
    suspend fun getUserInfo(token : String) : RecipeResult<CreatorRequest>
}