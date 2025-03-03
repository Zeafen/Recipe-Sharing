package com.receipts.receipt_sharing.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.PasswordChecker
import com.receipts.receipt_sharing.domain.response.AuthResult
import com.receipts.receipt_sharing.presentation.auth.AuthEvent
import com.receipts.receipt_sharing.presentation.auth.AuthPageState
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state: AuthPageState,
    onEvent: (AuthEvent) -> Unit,
    onOpenMenu: () -> Unit,
    onAuthorizationFinished: () -> Unit,
    onGotoRegister: () -> Unit,
    onGoToChangePassword : () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                },
                title = {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.login_page_header),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.headlineLarge
                    )
                })
        }
    ) {
        when (state.result) {
            is AuthResult.Authorized -> {
                onAuthorizationFinished()
            }

            is AuthResult.Error -> ErrorInfoPage(
                modifier = Modifier
                    .padding(it),
                errorInfo = state.result.data ?: stringResource(id = R.string.unknown_error_txt)
            ) {
                onEvent(AuthEvent.ClearData)
            }

            is AuthResult.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(84.dp),
                        strokeWidth = 8.dp
                    )
                }
            }

            is AuthResult.Unauthorized -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                        maxLines = 1,
                        singleLine = true,
                        value = state.login,
                        visualTransformation = if (!state.showPassword) PasswordVisualTransformation('*') else VisualTransformation.None,
                        onValueChange = { onEvent(AuthEvent.SetLogin(it)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.person_ic),
                                contentDescription = null
                            )
                        },
                        isError = state.login.isEmpty(),
                        supportingText = {
                            AnimatedVisibility(visible = state.login.isEmpty(),
                                enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                                    spring(stiffness = Spring.StiffnessLow)
                                ),
                                exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                                    spring(stiffness = Spring.StiffnessLow)
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.empty_field_error),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.W400,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.login_enter_str
                                )
                            )
                        }
                    )
                    OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                    maxLines = 1,
                    singleLine = true,
                    value = state.password,
                    visualTransformation = if (!state.showPassword) PasswordVisualTransformation('*') else VisualTransformation.None,
                    onValueChange = { onEvent(AuthEvent.SetPassword(it)) },
                    trailingIcon = {
                        IconButton(
                            onClick = { onEvent(AuthEvent.SetShowPassword(!state.showPassword)) }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (state.showPassword) R.drawable.hide_ic
                                    else R.drawable.show_ic
                                ),
                                contentDescription = null
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.passw_ic),
                            contentDescription = null
                        )
                    },
                    isError = !state.passwordOK,
                    supportingText = {
                        AnimatedVisibility(visible = !state.passwordOK,
                            enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                                spring(stiffness = Spring.StiffnessLow)
                            ),
                            exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                                spring(stiffness = Spring.StiffnessLow)
                            )
                        ) {
                            Text(
                                text = when {
                                    state.password.length < PasswordChecker.MinLength -> stringResource(
                                        R.string.incorrect_length_least_error,
                                        PasswordChecker.MinLength
                                    )

                                    state.password.count { it.isDigit() } < PasswordChecker.NumbersLeastCount -> stringResource(
                                        R.string.must_contain_least_numbers_error,
                                        PasswordChecker.NumbersLeastCount
                                    )

                                    state.password.count { it.isLetter() } < PasswordChecker.LettersLeastCount -> stringResource(
                                        R.string.must_contain_least_letters_error,
                                        PasswordChecker.LettersLeastCount
                                    )

                                    PasswordChecker.HasSpecials && !state.password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex()) -> stringResource(
                                        R.string.must_contain_specials_error
                                    )

                                    PasswordChecker.HasUpperCase && !state.password.contains("[A-Z]".toRegex()) -> stringResource(
                                        R.string.must_contain_uppercase
                                    )

                                    PasswordChecker.HasLowerCase && !state.password.contains("[a-z]".toRegex()) -> stringResource(
                                        R.string.must_contain_lowercase
                                    )

                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W400,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(
                                R.string.password_enter_str
                            )
                        )
                    }
                )
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onEvent(AuthEvent.ConfirmLogin) }) {
                        Text(text = stringResource(R.string.login_confirm_str))
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .alpha(0.3f)
                                .clickable {
                                    onGoToChangePassword()
                                },
                            text = stringResource(R.string.forgot_password_lbl),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier
                                .alpha(0.3f)
                                .clickable {
                                    onGotoRegister()
                                },
                            text = stringResource(R.string.register_confirm_str),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            var state by remember {
                mutableStateOf(
                    AuthPageState(
                        result = AuthResult.Unauthorized()
                    )
                )
            }
            LoginScreen(onGotoRegister = { /*TODO*/ }, state = state,
                onEvent = {
                    if (it is AuthEvent.SetPassword)
                        state = state.copy(password = it.password)
                },
                onOpenMenu = {},
                onAuthorizationFinished = {},
                onGoToChangePassword = {})
        }
    }
}