package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.CreatorRequest
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import retrofit2.HttpException

class CreatorsRepositoryImpl constructor(
    private val api : RecipesAPIService
) : CreatorsRepository {
    override suspend fun getCreators(token: String): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getCreators(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCreatorsByName(
        token: String,
        nickname: String
    ): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getCreatorsByName(token, nickname)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCreatorById(
        token: String,
        creatorId: String
    ): RecipeResult<CreatorRequest> {
        return try {
            val result = api.getCreatorById(token, creatorId)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFollows(token: String): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getFollows(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFollowers(token: String): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getFollowers(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFollowsByName(
        token: String,
        name: String
    ): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getFollowsByName(token, name)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getUserInfo(token: String): RecipeResult<CreatorRequest> {
        return try {
            val result = api.getUserInfo(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowers(
        token: String,
        creatorID: String
    ): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getCreatorFollowers(token, creatorID)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun addToFollows(token: String, creatorID: String): RecipeResult<Unit> {
        return try {
            api.addToFollows(token, creatorID)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun removeFromFollows(token: String, creatorID: String): RecipeResult<Unit> {
        return try {
            api.removeFromFollows(token, creatorID)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun doesFollow(token: String, creatorID: String): RecipeResult<Boolean> {
        return try {
            RecipeResult.Succeed(api.doesFollow(token, creatorID))
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun updateCreator(token: String, request: CreatorRequest): RecipeResult<Unit> {
        return try {
            api.updateCreator(token, request)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.message)
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }
}