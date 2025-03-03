package com.receipts.receipt_sharing.presentation.creators

import IRecipesRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.helpers.FileHelper
import com.receipts.receipt_sharing.data.helpers.PasswordChecker
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.creators.EmailConfirmRequest
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.response.RecipeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(
    private val recipesRepo: IRecipesRepository,
    private val creatorRepo: ICreatorsRepository
) : ViewModel() {
    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _userInfo: MutableStateFlow<RecipeResult<ProfileRequest>> =
        MutableStateFlow(RecipeResult.Downloading())
    private val _state: MutableStateFlow<ProfilePageState> = MutableStateFlow(ProfilePageState())

    val state: StateFlow<ProfilePageState> = combine(_state, _userInfo) { state, userInfo ->
        state.copy(
            creator = userInfo
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun onEvent(event: ProfilePageEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfilePageEvent.SetCreatorName -> _state.update {
                    it.copy(
                        creatorName = event.name,
                        isError = event.name.isEmpty() && state.value.creatorLogin.length < 10
                    )
                }

                is ProfilePageEvent.SetCreatorLogin -> _state.update {
                    it.copy(
                        creatorLogin = event.login,
                        isError = state.value.creatorName.isEmpty() && event.login.length < 10
                    )
                }

                is ProfilePageEvent.SetCreatorAboutMe -> _state.update {
                    it.copy(
                        creatorName = event.aboutMe,
                    )
                }

                is ProfilePageEvent.SetImageUrl -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    if (token != null) {
                        val file = FileHelper.get().getFileFromUri(event.imageUri)
                        file?.let { imageFile ->
                            val url = recipesRepo.uploadCreatorImage(
                                token,
                                File(imageFile)
                            )

                            url.data?.let { resPath ->
                                _state.update {
                                    it.copy(
                                        imageUrl = resPath
                                    )
                                }
                            }
                        }
                    }
                }

                ProfilePageEvent.SaveInfoChanges -> {
                    val creatorName = state.value.creatorName
                    val aboutMe = state.value.creatorAboutMe
                    val imageUrl = state.value.imageUrl
                    if (creatorName.isBlank())
                        return@launch
                    val creator = state.value.creator.data?.copy(
                        nickname = creatorName,
                        login = state.value.creatorLogin,
                        aboutMe = aboutMe ?: "",
                        imageUrl = imageUrl ?: state.value.creator.data!!.imageUrl
                    )
                    authDataStoreRepo.authDataStoreFlow.first().token?.let {
                        if (creator != null)
                            when (val info = creatorRepo.updateCreator(it, creator)) {
                                is RecipeResult.Error -> _state.update {
                                    it.copy(
                                        infoMessage = info.info ?: "Unknown error"
                                    )
                                }

                                else -> _state.update {
                                    it.copy(infoMessage = "Successfully updated!")
                                }
                            }
                        else _state.update {
                            it.copy(
                                infoMessage = "Cannot find user info. Please try to reload the page."
                            )
                        }
                    }

                    onEvent(ProfilePageEvent.UpdateUserState)
                }

                ProfilePageEvent.ConfirmChangePassword -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let {
                        when (val info = creatorRepo.updatePassword(
                            it,
                            ChangePasswRequest(state.value.creatorEmail, state.value.newPassword, state.value.emailCode)
                        )) {
                            is RecipeResult.Error -> _state.update {
                                it.copy(infoMessage = info.info ?: "Unknown error")
                            }

                            else -> _state.update {
                                it.copy(infoMessage = "Password changed")
                            }
                        }
                    } ?: _state.update {
                        it.copy(infoMessage = "Unauthorized")
                    }
                }

                ProfilePageEvent.LoadUserInfo -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    _userInfo.update {
                        RecipeResult.Downloading()
                    }

                    _userInfo.update {
                        token?.let {
                            creatorRepo.getUserInfo(it)
                        } ?: RecipeResult.Error()
                    }

                    if (_userInfo.value is RecipeResult.Succeed)
                        _state.update {
                            it.copy(
                                creatorName = _userInfo.value.data?.nickname ?: "",
                                creatorAboutMe = _userInfo.value.data?.aboutMe,
                                creatorLogin = _userInfo.value.data?.login ?: "",
                                creatorEmail = _userInfo.value.data?.email ?: "",
                                creatorEmailConfirmed = _userInfo.value.data?.emailConfirmed?:false,
                                imageUrl = _userInfo.value.data?.imageUrl,
                                followersCount = token?.let {
                                    creatorRepo.getFollowersCount(it).data ?: 0
                                } ?: 0,
                            )
                        }
                }

                ProfilePageEvent.UpdateUserState -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token
                    token?.let {
                        creatorRepo.getUserInfo(it).data?.let {
                            authDataStoreRepo.updateUserName(it.nickname)
                            authDataStoreRepo.updateImageUrl(it.imageUrl)
                        }
                    }
                }

                is ProfilePageEvent.SetCurrentScreen -> _state.update {
                    it.copy(screen = event.screen)
                }

                ProfilePageEvent.DiscardChanges -> {
                    if (state.value.creator.data != null)
                        _state.update {
                            it.copy(
                                creatorName = state.value.creator.data!!.nickname,
                                creatorLogin = state.value.creator.data!!.login,
                                creatorAboutMe = state.value.creator.data!!.aboutMe,
                                creatorEmail = state.value.creator.data!!.email,
                                imageUrl = state.value.creator.data!!.imageUrl,
                                newPassword = "",
                                repeatPassword = "",
                                passwordOk = false,
                                passwordsMatch = false,
                                emailCode = "",
                                isError = false
                            )
                        }
                    else
                        onEvent(ProfilePageEvent.LoadUserInfo)

                }

                is ProfilePageEvent.SetOpenConfirmExitDialog -> _state.update {
                    it.copy(openConfirmExitDialog = event.openDialog)
                }

                ProfilePageEvent.LogOut -> {
                    authDataStoreRepo.updateUserName("")
                    authDataStoreRepo.updateImageUrl("")
                    authDataStoreRepo.updateUserToken(null)
                }

                is ProfilePageEvent.SetPassword -> _state.update {
                    it.copy(
                        newPassword = event.newPassword,
                        passwordOk = PasswordChecker.checkPassword(event.newPassword),
                    )
                }

                is ProfilePageEvent.SetEmailCode -> _state.update {
                    it.copy(
                        emailCode = event.recoveryToken
                    )
                }

                is ProfilePageEvent.SetEmail -> _state.update {
                    it.copy(emailCode = event.email)
                }

                is ProfilePageEvent.SetRepeatPassword -> _state.update {
                    val passwordsMatch = state.value.newPassword == state.value.repeatPassword
                    it.copy(
                        repeatPassword = event.repeatPassword,
                        passwordsMatch = passwordsMatch,
                    )
                }

                is ProfilePageEvent.SetShowPassword -> _state.update {
                    it.copy(showPassword = event.showPassword)
                }

                ProfilePageEvent.GetEmailCode -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let {
                        when (val info =
                            creatorRepo.getCode(it)) {
                            is RecipeResult.Error -> _state.update {
                                it.copy(
                                    infoMessage = info.info ?: "Unknown error"
                                )
                            }

                            else -> _state.update {
                                it.copy(
                                    infoMessage = "Code has been sent"
                                )
                            }
                        }
                    } ?: _state.update {
                        it.copy(infoMessage = "Unauthorized")
                    }
                }

                ProfilePageEvent.SetEmailGetCode -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let {
                        when (val info =
                            creatorRepo.setEmailGetCode(it, state.value.creatorEmail)) {
                            is RecipeResult.Error -> _state.update {
                                it.copy(
                                    infoMessage = info.info ?: "Unknown error"
                                )
                            }

                            else -> _state.update {
                                it.copy(
                                    infoMessage = "Code has been sent"
                                )
                            }
                        }
                    } ?: _state.update {
                        it.copy(infoMessage = "Unauthorized")
                    }
                }

                ProfilePageEvent.ChangeEmail -> {
                    authDataStoreRepo.authDataStoreFlow.first().token?.let {
                        when (val info = creatorRepo.confirmEmail(
                            it,
                            EmailConfirmRequest(state.value.creatorEmail, state.value.emailCode)
                        )) {
                            is RecipeResult.Error -> _state.update {
                                it.copy(infoMessage = info.info ?: "Unknown error")
                            }

                            else -> _state.update {
                                it.copy(
                                    infoMessage = "Email confirmed",
                                    creatorEmailConfirmed = true
                                )
                            }
                        }
                    } ?: _state.update {
                        it.copy(infoMessage = "Unauthorized")
                    }
                }

                is ProfilePageEvent.SetOpenEditEmailDialog -> _state.update {
                    it.copy(openEditEmailDialog = event.openDialog)
                }

                ProfilePageEvent.ClearInfo -> _state.update {
                    it.copy(infoMessage = null)
                }
            }
        }
    }
}


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
    data object SaveInfoChanges : ProfilePageEvent
    data object ConfirmChangePassword : ProfilePageEvent
    data object DiscardChanges : ProfilePageEvent
    data object LogOut : ProfilePageEvent
    data object GetEmailCode : ProfilePageEvent
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