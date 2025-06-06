package com.receipts.receipt_sharing.domain.repositories

import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.creators.EmailConfirmRequest
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.PagedResult

interface CreatorsRepository {

    /**
     * Attempts to load All creators
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getCreators(token : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get the most high rated creators
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getTopCreators(token : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get creators, filtered by name
     * @param token Authorization token
     * @param nickname Creators searched name
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getCreatorsByName(token : String, nickname : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get creator by id
     * @param token Authorization token
     * @param creatorId Creator identifier
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creator has successfully been loaded
     */
    suspend fun getCreatorById(token : String, creatorId : String) : ApiResult<CreatorRequest>

    /**
     * Attempts to get own followed creators
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getOwnFollows(token : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get own followed creators, filtered by name
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getOwnFollowsByName(token : String, name : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get own followed creators count
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] count has successfully been obtained
     */
    suspend fun getOwnFollowsCount(token : String) : ApiResult<Long>

    /**
     * Attempts to get own followers
     * @param token Authorization token
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getOwnFollowers(token : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get own followers, filtered by name
     * @param token Authorization token
     * @param name creators searched name
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getOwnFollowersByName(token : String, name : String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get own followers count
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] count has successfully been obtained
     */
    suspend fun getOwnFollowersCount(token : String) : ApiResult<Long>

    /**
     * Attempts to get creator's followers
     * @param token Authorization token
     * @param creatorID Creator identifier to get followers which for
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getCreatorFollowers(token : String, creatorID: String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get creator's followers, filtered by name
     * @param token Authorization token
     * @param creatorID Creator identifier to get followers which for
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getCreatorFollowersByName(token : String, name : String, creatorID: String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get creator's followers count
     * @param token Authorization token
     * @param creatorID Creator identifier to get followers which for
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] count has successfully been obtained
     */
    suspend fun getCreatorFollowersCount(token : String, creatorID : String) : ApiResult<Long>

    /**
     * Attempts to get users followed by creator
     * @param token Authorization token
     * @param creatorID Creator identifier
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getCreatorFollows(token : String, creatorID: String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get users followed by creator, filtered by name
     * @param token Authorization token
     * @param creatorID Creator identifier to get follows for
     * @param name Creators searched name
     * @param page Selected page
     * @param pageSize Items per page
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] creators have successfully been loaded
     */
    suspend fun getCreatorFollowsByName(token : String, name : String, creatorID: String, page : Int, pageSize : Int) : ApiResult<PagedResult<List<CreatorRequest>>>

    /**
     * Attempts to get amount of users followed by creator
     * @param token Authorization token
     * @param creatorID Creator identifier to get followes count for
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] count has successfully been obtained
     */
    suspend fun getCreatorFollowsCount(token : String, creatorID : String) : ApiResult<Long>

    /**
     * Attempts to get creator's recipes amount
     * @param token Authorization token
     * @param creatorID Creator identifier to get recipes count for
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] count has successfully been obtained
     */
    suspend fun getCreatorRecipesCount(token : String, creatorID : String) : ApiResult<Long>

    /**
     * Attempts to add creator to own follows
     * @param token Authorization token
     * @param creatorID Creator identifier to add to follows
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if creator has successfully been added to follows
     */
    suspend fun addToFollows(token : String, creatorID : String) : ApiResult<Unit>
    /**
     * Attempts to add creator to own follows
     * @param token Authorization token
     * @param creatorID Creator identifier to remove from follows
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if creator has successfully been removed to follows
     */
    suspend fun removeFromFollows(token : String, creatorID : String) : ApiResult<Unit>

    /**
     * Attempts to check if user is followed
     * @param token Authorization token
     * @param creatorID Creator identifier to check if it is followed by user
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if request succeed
     */
    suspend fun doesFollow(token : String, creatorID : String) : ApiResult<Boolean>

    //Emails

    /**
     * Attempts to set email to account
     * @param token Authorization token
     * @param email Email address to set
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if email has been successfully set
     */
    suspend fun setEmail(token : String, email : String) : ApiResult<Unit>

    /**
     * Attempts to set email to account and request confirmation code to be sent
     * @param token Authorization token
     * @param email Email address to set
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if email has bent set and code has been sent
     */
    suspend fun setEmailGetCode(token : String, email : String) : ApiResult<Unit>

    /**
     * Attempts to request confirmation code to e sent to bounded email address
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if confirmation code has been sent
     */
    suspend fun getCode(token : String) : ApiResult<Unit>

    /**
     * Attempts to confirm email
     * @param token Authorization token
     * @param request Request body
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if email has been confirmed
     */
    suspend fun confirmEmail(token : String, request : EmailConfirmRequest) : ApiResult<Unit>

    /**
     * Attempts to update user account
     * @param token Authorization token
     * @param request Profile update request body
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if user account has successfully been updated
     */
    suspend fun updateCreator(token : String, request : ProfileRequest) : ApiResult<Unit>

    /**
     * Attempts to update user password
     * @param token Authorization token
     * @param request Password update request body
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if user's password has successfully been updated
     */
    suspend fun updatePassword(token : String, request : ChangePasswRequest) : ApiResult<Unit>

    /**
     * Attempts to get user account info
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if account information has been obtained
     */
    suspend fun getUserInfo(token : String) : ApiResult<ProfileRequest>

    /**
     * Attempts to delete user account
     * @param token Authorization token
     * @return [ApiResult.Error] if request failed; [ApiResult.Succeed] if account has been successfully deleted
     */
    suspend fun deleteAccount(token : String) : ApiResult<Unit>
}