package com.receipts.receipt_sharing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@Composable
fun ErrorInfoPage(
    modifier: Modifier = Modifier,
    errorInfo : String,
    onReloadPage : () -> Unit) {
    Column(modifier = modifier
        .fillMaxSize()
        .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer)) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.error_info_title),
                    style = MaterialTheme.typography.headlineLarge
                )
                Icon(modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.error,
                    imageVector = Icons.Default.Warning,
                    contentDescription = "")

                Text(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .alpha(0.9f),
                    text = errorInfo,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Button(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = onReloadPage) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(id = R.string.reload_page_btn_txt)
                    )
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
            var errorSolved by rememberSaveable {
                mutableStateOf(false)
            }
            if(!errorSolved)
                ErrorInfoPage(modifier = Modifier
                    .padding(vertical = 12.dp),
                    errorInfo = "Cannot find any source of that creator") {
                    errorSolved = ! errorSolved
                }
            else
                Text(text = "Error solved")
        }
    }
}