package com.receipts.receipt_sharing.ui.infoPages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes error information page
 * @param modifier Modifier applied to ErrorInfoPage
 * @param onReloadPage called when user clicks on "Reload" button
 * @param errorInfo error info string
 */
@Composable
fun ErrorInfoPage(
    modifier: Modifier = Modifier,
    errorInfo: String,
    onReloadPage: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(0.8f),
            painter = painterResource(R.drawable.error_img),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Text(
            text = stringResource(R.string.error_info_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = TextUnit(
                0.1f,
                TextUnitType.Em
            ),
            fontWeight = FontWeight.W500
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp, start = 8.dp, end = 8.dp)
        ) {
            item {
                Text(
                    text = errorInfo,
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = TextUnit(
                        2f,
                        TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.W400
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = onReloadPage,
        ) {
            Icon(modifier = Modifier
                .padding(end = 8.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
            Text(text = stringResource(R.string.reload_page_btn_txt))
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
            if (!errorSolved)
                ErrorInfoPage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    errorInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eu mauris semper metus cursus rhoncus vitae vel odio. Sed iaculis aliquam nisl quis tincidunt. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus vestibulum tincidunt pharetra. Etiam ac lacus vel arcu elementum bibendum. Duis finibus orci nulla, vitae finibus lacus varius tincidunt. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed"
                ) {
                    errorSolved = !errorSolved
                }
            else
                Text(text = "Error solved")
        }
    }
}