package com.receipts.receipt_sharing.presentation.creators.profile

import android.net.Uri

sealed interface ProfilePageEvent {
    data object LoadUserInfo : ProfilePageEvent
    data object UpdateUserState : ProfilePageEvent
    data class SetCreatorName(val name: String) : ProfilePageEvent
    data class SetCreatorLogin(val login: String) : ProfilePageEvent
    data class SetCreatorAboutMe(val aboutMe: String) : ProfilePageEvent
    data class SetImageUrl(val imageUri: Uri?) : ProfilePageEvent
    data class SetCurrentScreen(val screen: ProfileConfigScreens) : ProfilePageEvent
    data class SetOpenConfirmExitDialog(val openDialog: Boolean) : ProfilePageEvent
    data class SetOpenEditEmailDialog(val openDialog: Boolean) : ProfilePageEvent
    data class SetOpenConfirmDeleteDialog(val openDialog: Boolean) : ProfilePageEvent
    data object SaveInfoChanges : ProfilePageEvent
    data object ConfirmChangePassword : ProfilePageEvent
    data object DiscardChanges : ProfilePageEvent
    data class LogOut(val onLogOut: () -> Unit) : ProfilePageEvent
    data class DeleteAccount(val onLogOut: () -> Unit) : ProfilePageEvent
    data object GetEmailCode : ProfilePageEvent
    data object GetCode : ProfilePageEvent
    data object SetEmailGetCode : ProfilePageEvent
    data object ChangeEmail : ProfilePageEvent
    data object ClearInfo : ProfilePageEvent

    //Edit Password
    data class SetPassword(val newPassword: String) : ProfilePageEvent
    data class SetShowPassword(val showPassword: Boolean) : ProfilePageEvent
    data class SetRepeatPassword(val repeatPassword: String) : ProfilePageEvent
    data class SetEmailCode(val recoveryToken: String) : ProfilePageEvent
    data class SetEmail(val email: String) : ProfilePageEvent
}