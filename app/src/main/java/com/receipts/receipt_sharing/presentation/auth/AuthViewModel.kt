package com.receipts.receipt_sharing.data.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.CreatorsRepositoryImpl
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.ui.auth.AuthPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepositoryImpl,
    private val creatorsRepo: CreatorsRepositoryImpl,
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
        when (event) {
            AuthEvent.ConfirmLogin -> {
                viewModelScope.launch {
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
            }

            AuthEvent.ConfirmRegister -> {
                viewModelScope.launch {
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
            }

            is AuthEvent.SetLogin -> _state.update {
                it.copy(login = event.login)
            }

            is AuthEvent.SetPassword -> _state.update {
                it.copy(password = event.password)
            }

            is AuthEvent.SetRepeatPassword -> _state.update {
                it.copy(repeatPassword = event.password)
            }

            AuthEvent.ClearData -> {
                _state.update {
                    it.copy(
                        login = "",
                        password = "",
                        repeatPassword = "",
                    )
                }
                _result.update {
                    AuthResult.Unauthorized()
                }
            }
        }
    }

}


sealed class AuthEvent {
    data class SetLogin(val login: String) : AuthEvent()
    data class SetPassword(val password: String) : AuthEvent()
    data class SetRepeatPassword(val password: String) : AuthEvent()
    data object ConfirmLogin : AuthEvent()
    data object ConfirmRegister : AuthEvent()
    data object ClearData : AuthEvent()
}