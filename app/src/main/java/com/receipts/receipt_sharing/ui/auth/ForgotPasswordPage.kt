package com.receipts.receipt_sharing.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
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
import com.receipts.receipt_sharing.presentation.auth.AuthEvent
import com.receipts.receipt_sharing.presentation.auth.AuthPageState
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    state: AuthPageState,
    onEvent: (AuthEvent) -> Unit,
    onGoBackClick: () -> Unit
) {
    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onGoBackClick) {
                        Icon(painter = painterResource(R.drawable.back_ic), contentDescription = "")
                    }
                },
                title = {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.change_password_lbl),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.headlineLarge
                    )
                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
                value = state.email,
                maxLines = 1,
                singleLine = true,
                onValueChange = { onEvent(AuthEvent.SetEmailCode(it)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.email_ic),
                        contentDescription = null
                    )
                },
                isError = state.emailCode.isEmpty(),
                supportingText = {
                    AnimatedVisibility(visible = state.email.isEmpty(),
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
                            R.string.email_address_lbl
                        )
                    )
                }
            )
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
                maxLines = 1,
                singleLine = true,
                value = state.emailCode,
                onValueChange = { onEvent(AuthEvent.SetEmailCode(it)) },
                trailingIcon = {
                    Button(modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                        enabled = state.email.isNotEmpty(),
                        onClick = { onEvent(AuthEvent.SendCode) }
                    ) {
                        Text(
                            text = stringResource(R.string.generate_lbl),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.email_ic),
                        contentDescription = null
                    )
                },
                isError = state.emailCode.isEmpty(),
                supportingText = {
                    AnimatedVisibility(visible = state.emailCode.isEmpty(),
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
                            R.string.email_code_lbl
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
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
                maxLines = 1,
                singleLine = true,
                value = state.repeatPassword,
                onValueChange = { onEvent(AuthEvent.SetRepeatPassword(it)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.passw_ic),
                        contentDescription = null
                    )
                },
                enabled = state.passwordOK,
                isError = !state.passwordsMatch,
                supportingText = {
                    AnimatedVisibility(visible = !state.passwordsMatch && state.passwordOK,
                        enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                            spring(stiffness = Spring.StiffnessLow)
                        ),
                        exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                            spring(stiffness = Spring.StiffnessLow)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.values_match_error),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(
                            R.string.password_repeat_str
                        )
                    )
                }
            )
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp),
                enabled = state.passwordOK && state.passwordsMatch && state.email.isNotEmpty() && state.emailCode.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                onClick = { onEvent(AuthEvent.ConfirmLogin) }) {
                Text(text = stringResource(R.string.change_password_lbl))
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            ForgotPasswordPage(
                state = AuthPageState(),
                onEvent = {},
                onGoBackClick = {}
            )
        }
    }
}