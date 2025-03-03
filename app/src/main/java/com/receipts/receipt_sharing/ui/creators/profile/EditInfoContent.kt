package com.receipts.receipt_sharing.ui.creators.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.presentation.creators.ProfilePageEvent
import com.receipts.receipt_sharing.presentation.creators.ProfilePageState
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme


@Composable
fun EditInfoContent(
    modifier: Modifier = Modifier,
    state: ProfilePageState,
    onEvent: (ProfilePageEvent) -> Unit
) {
    val photoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            onEvent(ProfilePageEvent.SetImageUrl(uri))
        }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
        ) {
            if (state.imageUrl.isNullOrEmpty())
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.4f),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(R.drawable.no_image),
                    contentDescription = ""
                )
            else
                AsyncImage(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .fillMaxWidth(0.4f),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.imageUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = UnsafeImageLoader.getInstance(),
                    contentScale = ContentScale.Fit,
                    contentDescription = "",
                )
            FilledIconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                onClick = {
                    photoLauncher.launch(
                        PickVisualMediaRequest
                            .Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .build()
                    )
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_ic),
                    contentDescription = null
                )
            }
        }

        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
            value = state.creatorName,
            onValueChange = { onEvent(ProfilePageEvent.SetCreatorName(it)) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.user_info_page_ic),
                    contentDescription = null
                )
            },
            isError = state.creatorName.isEmpty(),
            supportingText = {
                AnimatedVisibility(visible = state.creatorName.isEmpty(),
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
                        R.string.creator_name_input
                    )
                )
            }
        )
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
            value = state.creatorLogin,
            onValueChange = { onEvent(ProfilePageEvent.SetCreatorName(it)) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.user_info_page_ic),
                    contentDescription = null
                )
            },
            isError = state.creatorLogin.length < 10,
            supportingText = {
                AnimatedVisibility(visible = state.creatorName.isEmpty(),
                    enter = slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                        spring(stiffness = Spring.StiffnessLow)
                    ),
                    exit = slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                        spring(stiffness = Spring.StiffnessLow)
                    )
                ) {
                    Text(
                        text = if (state.creatorLogin.isEmpty())
                            stringResource(R.string.empty_field_error)
                        else if (state.creatorLogin.length < 10)
                            stringResource(R.string.incorrect_length_least_error, 10)
                        else "",
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
            .fillMaxHeight(0.7f)
            .padding(vertical = 24.dp, horizontal = 12.dp),
            value = state.creatorAboutMe ?: "",
            onValueChange = { onEvent(ProfilePageEvent.SetCreatorAboutMe(it)) },
            label = {
                Text(
                    text = stringResource(
                        R.string.creator_about_me_input
                    )
                )
            }
        )


    }
}

@Preview
@Composable
private fun Preview() {
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            Scaffold { innerPadding ->
                EditInfoContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
                    state = ProfilePageState(
                        creatorAboutMe = ""
                    ),
                    onEvent = {}
                )
            }
        }
    }
}