package com.receipts.receipt_sharing.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.helpers.PasswordChecker
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.repositories.IAuthRepository
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.domain.response.RecipeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepo: IAuthRepository,
    private val creatorsRepo: ICreatorsRepository,
) : ViewModel() {

    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _result = MutableStateFlow<AuthResult<String>>(AuthResult.Unauthorized())

    private val _state = MutableStateFlow(AuthPageState())

    val state = combine(_result, _state) { res, state ->
        state.copy(
            result = res
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), AuthPageState())


    init {
        viewModelScope.launch {
            val token = authDataStoreRepo.authDataStoreFlow.first().token

            _result.update {
                AuthResult.Loading()
            }
            _result.update {
                token?.let { tok ->
                    val userInfo = creatorsRepo.getUserInfo(tok)
                    if (userInfo.data != null) {
                        authDataStoreRepo.updateUserName(userInfo.data.nickname)
                        authDataStoreRepo.updateImageUrl(userInfo.data.imageUrl)
                    }
                    authRepo.authorize(tok)
                } ?: AuthResult.Unauthorized()
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        viewModelScope.launch {
            when (event) {
                AuthEvent.ConfirmLogin -> {
                    val login = state.value.login
                    val password = state.value.password
                    if (login.isBlank() || password.isBlank())
                        return@launch

                    _result.update {
                        AuthResult.Loading()
                    }
                    _result.update {
                        authRepo.logIn(login, password)
                    }
                    state.value.result.data?.let {
                        authDataStoreRepo.updateUserToken(it)
                        creatorsRepo.getUserInfo(it).data?.let {
                            authDataStoreRepo.updateUserName(it.nickname)
                            authDataStoreRepo.updateImageUrl(it.imageUrl)
                        }
                    }
                }

                AuthEvent.ConfirmRegister -> {
                    val login = state.value.login
                    val password = state.value.password
                    val repeatPassword = state.value.repeatPassword
                    if (login.isBlank() || password.isBlank() || repeatPassword.isBlank() || password != repeatPassword)
                        return@launch
                    _result.update {
                        AuthResult.Loading()
                    }
                    _result.update {
                        authRepo.register(login, password)
                    }
                    state.value.result.data?.let {
                        authDataStoreRepo.updateUserToken(it)
                        creatorsRepo.getUserInfo(it).data?.let {
                            authDataStoreRepo.updateUserName(it.nickname)
                            authDataStoreRepo.updateImageUrl(it.imageUrl)
                        }
                    }
                }

                AuthEvent.ResetPassword -> {
                    if (state.value.passwordOK && state.value.passwordsMatch) {
                        when (val result = authRepo.updatePassword(
                            ChangePasswRequest(
                                state.value.email, state.value.password, state.value.emailCode
                            )
                        )) {
                            is RecipeResult.Error -> _state.update {
                                it.copy(infoMessage = result.info ?: "Unknown error")
                            }

                            else -> _state.update {
                                it.copy(infoMessage = "Password changed")
                            }
                        }
                    } else _state.update {
                        it.copy(infoMessage = "Could not update password due to not all required fields were filled")
                    }
                }

                AuthEvent.SendCode -> {
                    if (state.value.email.isNotEmpty())
                        when (val result = authRepo.sendCode(state.value.email)) {
                            is RecipeResult.Error -> _state.update {
                                it.copy(infoMessage = result.info ?: "Unknown error")
                            }

                            else -> _state.update {
                                it.copy(infoMessage = "Code was sent")
                            }
                        }
                }

                is AuthEvent.SetLogin -> _state.update {
                    it.copy(login = event.login)
                }

                is AuthEvent.SetEmail -> _state.update {
                    it.copy(email = event.email)
                }

                is AuthEvent.SetEmailCode -> _state.update {
                    it.copy(emailCode = event.emailCode)
                }

                is AuthEvent.SetPassword -> _state.update {
                    it.copy(
                        password = event.password,
                        passwordOK = PasswordChecker.checkPassword(event.password)
                    )
                }

                is AuthEvent.SetRepeatPassword -> _state.update {
                    it.copy(
                        repeatPassword = event.password,
                        passwordsMatch = event.password == state.value.password
                    )
                }

                is AuthEvent.SetShowPassword -> _state.update {
                    it.copy(
                        showPassword = event.showPassword
                    )
                }

                AuthEvent.ClearData -> {
                    _state.update {
                        it.copy(
                            login = "",
                            email = "",
                            emailCode = "",
                            password = "",
                            repeatPassword = "",
                            passwordOK = false,
                            passwordsMatch = false
                        )
                    }
                    _result.update {
                        AuthResult.Unauthorized()
                    }
                }

                AuthEvent.ClearMessage -> _state.update {
                    it.copy(infoMessage = null)
                }
            }
        }
    }
}


sealed interface AuthEvent {
    data class SetLogin(val login: String) : AuthEvent
    data class SetEmail(val email: String) : AuthEvent
    data class SetEmailCode(val emailCode: String) : AuthEvent
    data class SetPassword(val password: String) : AuthEvent
    data class SetRepeatPassword(val password: String) : AuthEvent
    data class SetShowPassword(val showPassword: Boolean) : AuthEvent
    data object ConfirmLogin : AuthEvent
    data object ConfirmRegister : AuthEvent
    data object SendCode : AuthEvent
    data object ResetPassword : AuthEvent
    data object ClearData : AuthEvent
    data object ClearMessage : AuthEvent
}