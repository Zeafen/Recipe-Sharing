package com.receipts.receipt_sharing.ui.creators.profile

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.PasswordChecker
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun EditPasswordContent(
    modifier: Modifier = Modifier,
    emailCode : String,
    onEnterEmailCode : (String) -> Unit,
    password : String,
    onEnterPassword : (String) -> Unit,
    showPassword : Boolean = false,
    onSetShowPassword : (Boolean) -> Unit,
    repeatPassword : String,
    onEnterRepeatPassword : (String) -> Unit,
    onGenerateCodeClick : () -> Unit,
) {
    val passwordOk = remember(password) {
        PasswordChecker.checkPassword(password)
    }

    Column(modifier = modifier) {
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
                value = emailCode,
                onValueChange = onEnterEmailCode,
                trailingIcon = {
                    Button(modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                        onClick = onGenerateCodeClick
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
                isError = emailCode.isEmpty(),
                supportingText = {
                    AnimatedVisibility(visible = emailCode.isEmpty(),
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
            value = password,
            visualTransformation = if (!showPassword) PasswordVisualTransformation('*') else VisualTransformation.None,
            onValueChange = onEnterPassword,
            trailingIcon = {
                IconButton(
                    onClick = { onSetShowPassword(!showPassword) }
                ) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.hide_ic
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
            isError = !passwordOk,
            supportingText = {
                AnimatedVisibility(visible = !passwordOk,
                    enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                        spring(stiffness = Spring.StiffnessLow)
                    ),
                    exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                        spring(stiffness = Spring.StiffnessLow)
                    )
                ) {
                    Text(
                        text = when {
                            password.length < PasswordChecker.MinLength -> stringResource(
                                R.string.incorrect_length_least_error,
                                PasswordChecker.MinLength
                            )

                            password.count { it.isDigit() } < PasswordChecker.NumbersLeastCount -> stringResource(
                                R.string.must_contain_least_numbers_error,
                                PasswordChecker.NumbersLeastCount
                            )

                            password.count { it.isLetter() } < PasswordChecker.LettersLeastCount -> stringResource(
                                R.string.must_contain_least_letters_error,
                                PasswordChecker.LettersLeastCount
                            )

                            PasswordChecker.HasSpecials && !password.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex()) -> stringResource(
                                R.string.must_contain_specials_error
                            )

                            PasswordChecker.HasUpperCase && !password.contains("[A-Z]".toRegex()) -> stringResource(
                                R.string.must_contain_uppercase
                            )

                            PasswordChecker.HasLowerCase && !password.contains("[a-z]".toRegex()) -> stringResource(
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
            value = repeatPassword,
            onValueChange = onEnterRepeatPassword,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.passw_ic),
                    contentDescription = null
                )
            },
            enabled = passwordOk,
            isError = password != repeatPassword,
            supportingText = {
                AnimatedVisibility(visible = password != repeatPassword && passwordOk,
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
    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme {
        Surface {
            Scaffold { innerPadding ->
                EditPasswordContent(modifier = Modifier
                    .padding(innerPadding),
                    password = "",
                    onEnterPassword = {},
                    repeatPassword = "",
                    onEnterRepeatPassword = {},
                    onSetShowPassword = {},
                    onGenerateCodeClick = {},
                    emailCode = "",
                    onEnterEmailCode = {}
                )
            }
        }
    }
}