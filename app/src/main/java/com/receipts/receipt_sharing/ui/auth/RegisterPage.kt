package com.receipts.receipt_sharing.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.response.AuthResult
import com.receipts.receipt_sharing.domain.viewModels.AuthEvent
import com.receipts.receipt_sharing.ui.ErrorInfoPage
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onGotoLogin : () -> Unit,
    state : AuthPageState,
    onEvent: (AuthEvent) -> Unit,
    onOpenMenu : () -> Unit,
    onAuthorizationFinished : () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                },
                title = {
                    Text(modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                        text = stringResource(id = R.string.register_page_header),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.headlineLarge)
                })
        }
    ) {
        when(state.result){
            is AuthResult.Authorized -> {
                onAuthorizationFinished()
            }
            is AuthResult.Error -> ErrorInfoPage(modifier = Modifier
                .padding(it),
                errorInfo = state.result.data?: stringResource(id = R.string.unknown_error_txt)) {
                onEvent(AuthEvent.ClearData)
            }
            is AuthResult.Loading -> {
                Column(modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier
                        .size(84.dp),
                        strokeWidth = 8.dp
                    )
                }
            }
            is AuthResult.Unauthorized -> {
                Column(modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(modifier = Modifier
                        .padding(vertical = 8.dp),
                        value = state.login,
                        label = { Text(text = stringResource(R.string.login_enter_str)) },
                        onValueChange = { onEvent(AuthEvent.SetLogin(it)) })
                    OutlinedTextField(modifier = Modifier
                        .padding(vertical = 8.dp),
                        value = state.password,
                        label = { Text(text = stringResource(R.string.password_enter_str)) },
                        onValueChange = { onEvent(AuthEvent.SetPassword(it)) })
                    OutlinedTextField(modifier = Modifier
                        .padding(vertical = 8.dp),
                        value = state.repeatPassword,
                        label = { Text(text = stringResource(R.string.password_repeat_str)) },
                        onValueChange = { onEvent(AuthEvent.SetRepeatPassword(it)) })
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onEvent(AuthEvent.ConfirmRegister) }) {
                        Text(text = stringResource(R.string.register_confirm_str))
                    }
                    Text(modifier = Modifier
                        .alpha(0.3f)
                        .clickable {
                            onGotoLogin()
                        },
                        text = stringResource(R.string.go_to_login_str),
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            RegisterScreen(onGotoLogin = { /*TODO*/ },
                state = AuthPageState(
                result = AuthResult.Loading()
            ),
                onEvent = {},
                onOpenMenu = {},
                onAuthorizationFinished = {})
        }
    }
}