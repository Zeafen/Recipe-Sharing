package com.receipts.receipt_sharing.presentation.creators

import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.response.RecipeResult

data class ProfilePageState(
    val creator : RecipeResult<ProfileRequest> = RecipeResult.Downloading(),
    val screen : ProfileConfigScreens = ProfileConfigScreens.MainScreen,
    val openConfirmExitDialog : Boolean = false,
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
    val passwordOk : Boolean = false,
    val passwordsMatch : Boolean = false,

    val infoMessage : String? = null
)
