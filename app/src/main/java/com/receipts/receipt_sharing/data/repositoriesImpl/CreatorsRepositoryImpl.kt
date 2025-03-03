package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.creators.EmailConfirmRequest
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import retrofit2.HttpException

class CreatorsRepositoryImpl(
    private val api: RecipesAPIService
) : ICreatorsRepository {
    override suspend fun getCreators(token: String): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getCreators(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFollowersCount(token: String): RecipeResult<Long> {
        return try {
            RecipeResult.Succeed(api.getFollowersCount(token))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowersCount(
        token: String,
        creatorID: String
    ): RecipeResult<Long> {
        return try {
            RecipeResult.Succeed(api.getCreatorFollowersCount(token, creatorID))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCreatorRecipesCount(
        token: String,
        creatorID: String
    ): RecipeResult<Long> {
        return try {
            RecipeResult.Succeed(api.getRecipesCountByCreator(token, creatorID))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
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
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowsCount(
        token: String,
        creatorID: String
    ): RecipeResult<Long> {
        return try {
            val result = api.getCreatorFollowsCount(token, creatorID)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun setEmail(token: String, email: String): RecipeResult<Unit> {
        return try {
            api.setEmail(token, email)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun setEmailGetCode(token: String, email: String): RecipeResult<Unit> {
        return try {
            api.setEmailGetCode(token, email)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getCode(token: String): RecipeResult<Unit> {
        return try {
            api.getCode(token)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun confirmEmail(
        token: String,
        request: EmailConfirmRequest
    ): RecipeResult<Unit> {
        return try {
            api.confirmEmail(token, request)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun updatePassword(
        token: String,
        request: ChangePasswRequest
    ): RecipeResult<Unit> {
        return try {
            api.updatePassword(token, request)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
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
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFollows(token: String): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getFollows(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getFollowers(token: String): RecipeResult<List<CreatorRequest>> {
        return try {
            val result = api.getFollowers(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
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
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun getUserInfo(token: String): RecipeResult<ProfileRequest> {
        return try {
            val result = api.getUserInfo(token)
            RecipeResult.Succeed(result)
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
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
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun addToFollows(token: String, creatorID: String): RecipeResult<Unit> {
        return try {
            api.addToFollows(token, creatorID)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun removeFromFollows(token: String, creatorID: String): RecipeResult<Unit> {
        return try {
            api.removeFromFollows(token, creatorID)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun doesFollow(token: String, creatorID: String): RecipeResult<Boolean> {
        return try {
            RecipeResult.Succeed(api.doesFollow(token, creatorID))
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }

    override suspend fun updateCreator(token: String, request: ProfileRequest): RecipeResult<Unit> {
        return try {
            api.updateCreator(token, request)
            RecipeResult.Succeed()
        } catch (e: HttpException) {
            RecipeResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            RecipeResult.Error(e.message)
        }
    }
}