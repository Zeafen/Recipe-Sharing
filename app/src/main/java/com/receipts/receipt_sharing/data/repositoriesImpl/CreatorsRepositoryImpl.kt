package com.receipts.receipt_sharing.data.repositoriesImpl

import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.creators.EmailConfirmRequest
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.PagedResult
import retrofit2.HttpException

class CreatorsRepositoryImpl(
    private val api: RecipesAPIService
) : CreatorsRepository {
    override suspend fun getCreators(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getCreators(token, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getTopCreators(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getTopCreators(token, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorsByName(
        token: String,
        nickname: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getCreatorsByName(token, nickname, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorById(
        token: String,
        creatorId: String
    ): ApiResult<CreatorRequest> {
        return try {
            val result = api.getCreatorById(token, creatorId)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnFollows(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getFollows(token, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnFollowsByName(
        token: String,
        name: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getFollowsByName(token, name, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnFollowsCount(token: String): ApiResult<Long> {
        return try {
            val result = api.getFollowsCount(token)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnFollowers(
        token: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getFollowers(token, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnFollowersByName(
        token: String,
        name: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getFollowersByName(token, name, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getOwnFollowersCount(token: String): ApiResult<Long> {
        return try {
            ApiResult.Succeed(api.getFollowersCount(token))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }


    override suspend fun getCreatorFollowers(
        token: String,
        creatorID: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getCreatorFollowers(token, creatorID, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowersByName(
        token: String,
        name: String,
        creatorID: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getCreatorFollowersByName(token, creatorID, name, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowersCount(
        token: String,
        creatorID: String
    ): ApiResult<Long> {
        return try {
            ApiResult.Succeed(api.getCreatorFollowersCount(token, creatorID))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollows(
        token: String,
        creatorID: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getCreatorFollows(token, creatorID, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowsByName(
        token: String,
        name: String,
        creatorID: String,
        page: Int,
        pageSize: Int
    ): ApiResult<PagedResult<List<CreatorRequest>>> {
        return try {
            val result = api.getCreatorFollowsByName(token, creatorID, name, page, pageSize)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorFollowsCount(
        token: String,
        creatorID: String
    ): ApiResult<Long> {
        return try {
            val result = api.getCreatorFollowsCount(token, creatorID)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCreatorRecipesCount(
        token: String,
        creatorID: String
    ): ApiResult<Long> {
        return try {
            ApiResult.Succeed(api.getRecipesCountByCreator(token, creatorID))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }


    override suspend fun setEmail(token: String, email: String): ApiResult<Unit> {
        return try {
            api.setEmail(token, email)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun setEmailGetCode(token: String, email: String): ApiResult<Unit> {
        return try {
            api.setEmailGetCode(token, email)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getCode(token: String): ApiResult<Unit> {
        return try {
            api.getCode(token)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun confirmEmail(
        token: String,
        request: EmailConfirmRequest
    ): ApiResult<Unit> {
        return try {
            api.confirmEmail(token, request)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun updatePassword(
        token: String,
        request: ChangePasswRequest
    ): ApiResult<Unit> {
        return try {
            api.updatePassword(token, request)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun addToFollows(token: String, creatorID: String): ApiResult<Unit> {
        return try {
            api.addToFollows(token, creatorID)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun removeFromFollows(token: String, creatorID: String): ApiResult<Unit> {
        return try {
            api.removeFromFollows(token, creatorID)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun doesFollow(token: String, creatorID: String): ApiResult<Boolean> {
        return try {
            ApiResult.Succeed(api.doesFollow(token, creatorID))
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun updateCreator(token: String, request: ProfileRequest): ApiResult<Unit> {
        return try {
            api.updateCreator(token, request)
            ApiResult.Succeed()
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getUserInfo(token: String): ApiResult<ProfileRequest> {
        return try {
            val result = api.getUserInfo(token)
            ApiResult.Succeed(result)
        } catch (e: HttpException) {
            ApiResult.Error(e.response()?.message() ?: e.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }
}