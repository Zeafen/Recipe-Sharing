package com.receipts.receipt_sharing.presentation.creators.profile

import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.ValidationInfo

data class ProfilePageState(
    val creator : ApiResult<ProfileRequest> = ApiResult.Downloading(),
    val screen : ProfileConfigScreens = ProfileConfigScreens.MainScreen,
    val openConfirmExitDialog : Boolean = false,
    val openConfirmDeleteDialog : Boolean = false,
    val openEditEmailDialog : Boolean = false,
    val creatorName : String = "",
    val creatorEmail : String = "",
    val creatorEmailConfirmed : Boolean = false,
    val creatorLogin : String = "",
    val creatorAboutMe : String? = "",
    val imageUrl : String? = "",
    val isError : Boolean = false,
    val followersCount : Long = 0,
    val followsCount : Long = 0,

    //EditPassword
    val emailCode : String = "",
    val newPassword : String = "",
    val showPassword : Boolean = false,
    val repeatPassword : String = "",
    val passwordOk : ValidationInfo = ValidationInfo(),
    val passwordsMatch : Boolean = false,
    val infoMessage : String? = null
)