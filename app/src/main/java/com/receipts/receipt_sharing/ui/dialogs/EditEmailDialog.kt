package com.receipts.receipt_sharing.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes email editing dialog
 * @param email email address value
 * @param onEnterEmail called when user enters email address
 * @param emailCode email confirmation code value
 * @param onEnterCode called when user enters new confirmation code
 * @param onGenerateCodeClick called when user clicks on "Generate code" button
 * @param onConfirmClick called when user clicks on "Confirm" button
 * @param onDismissRequest called when user tries to dismiss dialog
 */
@Composable
fun EditEmailDialog(
    email: String,
    onEnterEmail: (String) -> Unit,
    emailCode: String,
    onEnterCode: (String) -> Unit,
    onGenerateCodeClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val emailOk = remember(email){
        email.matches(Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE))
    }
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .drawWithContent {
                        drawContent()
                        val contentSize = drawContext.size
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color.DarkGray,
                                    Color.LightGray
                                )
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(contentSize.width, 0f),
                            strokeWidth = 5f
                        )
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color.DarkGray,
                                    Color.LightGray
                                )
                            ),
                            start = Offset(0f, contentSize.height),
                            end = Offset(contentSize.width, contentSize.height),
                            strokeWidth = 5f
                        )
                    },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.email_address_lbl)
            )
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
                value = email,
                onValueChange = onEnterEmail,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.email_ic),
                        contentDescription = null
                    )
                },
                singleLine = true,
                isError = !emailOk,
                supportingText = {
                    AnimatedVisibility(visible = !emailOk,
                        enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                            spring(stiffness = Spring.StiffnessLow)
                        ),
                        exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                            spring(stiffness = Spring.StiffnessLow)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.email_format_error),
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
                value = emailCode,
                onValueChange = onEnterCode,
                trailingIcon = {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        enabled = email.isNotEmpty(),
                        onClick = onGenerateCodeClick,
                    ) {
                        Text(
                            text = stringResource(R.string.generate_lbl),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.key_ic),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 12.dp)
                        .weight(1f),
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = email.isNotEmpty() && emailCode.isNotEmpty(),
                    onClick = onConfirmClick
                ) {
                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.confirm_txt)
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onDismissRequest
                ) {
                    Text(
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(id = R.string.cancel_txt)
                    )
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
            EditEmailDialog(
                email = "someemail@gmail.com",
                emailCode = "",
                onEnterEmail = {},
                onEnterCode = {},
                onGenerateCodeClick = {},
                onDismissRequest = {},
                onConfirmClick = {}
            )
            Column(modifier = Modifier.fillMaxSize()) {
                repeat(100) {
                    Text("Text $it")
                }
            }
        }
    }
}