package com.receipts.receipt_sharing.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.helpers.PasswordChecker
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.creators.ChangePasswRequest
import com.receipts.receipt_sharing.domain.repositories.AuthRepository
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.presentation.ValidationInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepo: AuthRepository,
    private val creatorsRepo: CreatorsRepository,
) : ViewModel() {

    private val authDataStoreRepo = AuthDataStoreRepository.get()

    private val _result = MutableStateFlow<AuthResult<String>>(AuthResult.Loading())

    private val _state = MutableStateFlow(AuthPageState())

    val state = combine(_result, _state) { res, state ->
        state.copy(
            result = res
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), AuthPageState())


    /**
     * Processes Auth page events
     * @param event Auth page event
     * @see [AuthEvent]
     */
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
                    val result = authRepo.logIn(login, password).also {
                        it.data?.let {
                            authDataStoreRepo.updateSelectedPageIndex(0)
                            authDataStoreRepo.updateUserToken(it)
                            creatorsRepo.getUserInfo(it).data?.let {
                                authDataStoreRepo.updateUserName(it.nickname)
                                authDataStoreRepo.updateImageUrl(it.imageUrl)
                            }
                        }
                    }
                    _result.update {
                        result
                    }

                }

                AuthEvent.ConfirmRegister -> {
                    val login = state.value.login
                    val password = state.value.password
                    val email = state.value.email
                    val repeatPassword = state.value.repeatPassword
                    if (login.isBlank() || password.isBlank() || repeatPassword.isBlank() || password != repeatPassword)
                        return@launch
                    _result.update {
                        AuthResult.Loading()
                    }
                    val token = authRepo.register(login, email, password)
                    token.data?.let {
                        authDataStoreRepo.updateUserToken(it)
                        creatorsRepo.getUserInfo(it).data?.let {
                            authDataStoreRepo.updateUserName(it.nickname)
                            authDataStoreRepo.updateImageUrl(it.imageUrl)
                        }
                    }
                    _result.update {
                        token
                    }
                }

                AuthEvent.ResetPassword -> {
                    if (state.value.passwordValidation.isValid && state.value.passwordsMatch) {
                        when (val result = authRepo.updatePassword(
                            ChangePasswRequest(
                                state.value.email, state.value.password, state.value.emailCode
                            )
                        )) {
                            is ApiResult.Error -> _state.update {
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

                AuthEvent.Authorize -> {
                    val token = authDataStoreRepo.authDataStoreFlow.first().token

                    _result.update {
                        AuthResult.Loading()
                    }
                    _result.update {
                        token?.let { tok ->
                            val result = authRepo.authorize(tok)
                            when (result) {
                                is AuthResult.Authorized -> {
                                    authDataStoreRepo.updateUserToken(tok)
                                    creatorsRepo.getUserInfo(tok).data?.let {
                                        authDataStoreRepo.updateUserName(it.nickname)
                                        authDataStoreRepo.updateImageUrl(it.imageUrl)
                                    }
                                }

                                is AuthResult.Error -> {
                                    authDataStoreRepo.updateUserToken(null)
                                    authDataStoreRepo.updateUserName("")
                                    authDataStoreRepo.updateImageUrl("")
                                }

                                else -> {}
                            }
                            result
                        } ?: AuthResult.Unauthorized()
                    }
                }

                AuthEvent.SendCode -> {
                    if (state.value.email.isNotEmpty())
                        when (val result = authRepo.sendCode(state.value.email)) {
                            is ApiResult.Error -> _state.update {
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
                    it.copy(
                        email = event.email,
                        emailOk = event.email.matches(
                            Regex(
                                "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}",
                                RegexOption.IGNORE_CASE
                            )
                        )
                    )
                }

                is AuthEvent.SetEmailCode -> _state.update {
                    it.copy(emailCode = event.emailCode)
                }

                is AuthEvent.SetPassword -> _state.update {
                    it.copy(
                        password = event.password,
                        passwordValidation = PasswordChecker.checkPassword(event.password)
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
                            passwordValidation = ValidationInfo(),
                            passwordsMatch = false,
                            emailOk = false
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
