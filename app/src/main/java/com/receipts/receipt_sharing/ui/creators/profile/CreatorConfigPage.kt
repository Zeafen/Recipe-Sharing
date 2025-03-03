package com.receipts.receipt_sharing.ui.creators.profile

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.PasswordChecker
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.domain.creators.ProfileRequest
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.presentation.creators.ProfileConfigScreens
import com.receipts.receipt_sharing.presentation.creators.ProfilePageEvent
import com.receipts.receipt_sharing.presentation.creators.ProfilePageState
import com.receipts.receipt_sharing.ui.SectionSelectionButton
import com.receipts.receipt_sharing.ui.TwoLayerTopAppBar
import com.receipts.receipt_sharing.ui.dialogs.EditEmailDialog
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.infoPages.ErrorInfoPage
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorConfigPage(
    modifier: Modifier = Modifier,
    state: ProfilePageState,
    onEvent: (ProfilePageEvent) -> Unit,
    onOpenMenu: () -> Unit,
    onLogOut: () -> Unit
) {
    val ctx = LocalContext.current
    LaunchedEffect(state.infoMessage) {
        if (!state.infoMessage.isNullOrEmpty()) {
            Toast.makeText(ctx, state.infoMessage, Toast.LENGTH_SHORT).show()
            onEvent(ProfilePageEvent.ClearInfo)
        }
    }

    val refreshState = rememberPullToRefreshState()
    Scaffold(modifier = modifier,
        topBar = {
            TwoLayerTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth(),
                        text = stringResource(R.string.profile_page_header),
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = TextUnit(0.1f, TextUnitType.Em),
                        fontWeight = FontWeight.W400
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.screen == ProfileConfigScreens.MainScreen)
                            onOpenMenu()
                        else {
                            onEvent(ProfilePageEvent.SetCurrentScreen(ProfileConfigScreens.MainScreen))
                            onEvent(ProfilePageEvent.DiscardChanges)
                        }
                    }) {
                        AnimatedContent(state.screen) { targetState ->
                            if (targetState == ProfileConfigScreens.MainScreen)
                                Icon(painterResource(R.drawable.menu_ic), contentDescription = "")
                            else
                                Icon(painterResource(R.drawable.back_ic), contentDescription = "")
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(
                        state.screen == ProfileConfigScreens.EditInfo,
                        enter = expandHorizontally(tween(500, 500)) + fadeIn(
                            tween(500, 500)
                        )
                    ) {
                        IconButton(onClick = { onEvent(ProfilePageEvent.DiscardChanges) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                    }
                },
                additionalContent = {
                    AnimatedVisibility(
                        visible = state.screen in arrayOf(
                            ProfileConfigScreens.MainScreen,
                            ProfileConfigScreens.Security,
                            ProfileConfigScreens.ChangePassword
                        ),
                        enter = expandVertically(
                            tween(durationMillis = 500, delayMillis = 500),
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            spring(stiffness = Spring.StiffnessMediumLow)
                        ),
                        exit = shrinkVertically(
                            spring(stiffness = Spring.StiffnessMedium),
                            shrinkTowards = Alignment.Top
                        ) + fadeOut(
                            spring(stiffness = Spring.StiffnessMediumLow)
                        )
                    ) {
                        if (state.creator.data != null)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (state.imageUrl.isNullOrEmpty())
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth(0.4f)
                                            .padding(8.dp),
                                        contentScale = ContentScale.Crop,
                                        painter = painterResource(R.drawable.no_image),
                                        contentDescription = ""
                                    )
                                else
                                    AsyncImage(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .fillMaxWidth(0.4f)
                                            .padding(8.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.creator.data.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        imageLoader = UnsafeImageLoader.getInstance(),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = "",
                                    )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            style = MaterialTheme.typography.titleMedium,
                                            text = state.creator.data.nickname,
                                            textAlign = TextAlign.Start,
                                            fontWeight = FontWeight.W500,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            letterSpacing = TextUnit(0.15f, TextUnitType.Em)
                                        )
                                        Text(
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .alpha(0.5f),
                                            style = MaterialTheme.typography.bodyMedium,
                                            text = stringResource(
                                                R.string.account_number_str,
                                                state.creator.data.userID
                                            ),
                                            textAlign = TextAlign.Start,
                                            fontWeight = FontWeight.W300,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            letterSpacing = TextUnit(0.15f, TextUnitType.Em)
                                        )
                                    }
                                }

                            }
                        else
                            Column {
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .size(256.dp)
                                        .shimmerEffect()
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .align(Alignment.Start)
                                        .height(32.dp)
                                        .fillMaxWidth()
                                        .shimmerEffect()
                                )
                            }
                    }
                })
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.screen in arrayOf(
                    ProfileConfigScreens.EditInfo,
                    ProfileConfigScreens.ChangePassword
                ),
                enter = slideInVertically(
                    tween(durationMillis = 500, delayMillis = 500),
                    initialOffsetY = { offset -> offset }) + fadeIn(
                    tween(
                        durationMillis = 500,
                        delayMillis = 500
                    )
                ),
                exit = slideOutVertically(
                    spring(stiffness = Spring.StiffnessMediumLow),
                    targetOffsetY = { offset -> offset }) + fadeOut(spring(stiffness = Spring.StiffnessLow))
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    onClick = {
                        when {
                            state.screen == ProfileConfigScreens.EditInfo -> onEvent(
                                ProfilePageEvent.SaveInfoChanges
                            )

                            state.screen == ProfileConfigScreens.ChangePassword -> onEvent(
                                ProfilePageEvent.ConfirmChangePassword
                            )
                        }
                    },
                    enabled = when {
                        state.screen == ProfileConfigScreens.EditInfo -> !state.isError
                        state.screen == ProfileConfigScreens.ChangePassword -> state.passwordOk && state.passwordsMatch && state.emailCode.isNotEmpty()
                        else -> false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.save_changes_str),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.creator is RecipeResult.Downloading,
            onRefresh = { onEvent(ProfilePageEvent.LoadUserInfo) },
        ) {
            when (state.creator) {
                is RecipeResult.Downloading -> {
                    Column {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 24.dp)
                                    .height(32.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterHorizontally)
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                is RecipeResult.Error -> {
                    ErrorInfoPage(
                        errorInfo = state.creator.info ?: stringResource(
                            id = R.string.unknown_error_txt
                        ),
                        onReloadPage = { onEvent(ProfilePageEvent.LoadUserInfo) }
                    )
                }

                is RecipeResult.Succeed -> {
                    state.creator.data?.let {
                        if (state.openConfirmExitDialog)
                            AlertDialog(
                                onDismissRequest = {
                                    onEvent(
                                        ProfilePageEvent.SetOpenConfirmExitDialog(
                                            false
                                        )
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        onClick = {
                                            onEvent(ProfilePageEvent.LogOut)
                                            onLogOut()
                                        },
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = stringResource(R.string.confirm_txt),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleLarge,
                                            letterSpacing = TextUnit(
                                                1.5f,
                                                TextUnitType.Sp
                                            ),
                                            fontWeight = FontWeight.W500
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        ),
                                        onClick = {
                                            onEvent(ProfilePageEvent.SetOpenConfirmExitDialog(false))
                                        },
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = stringResource(R.string.cancel_changes_str),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleLarge,
                                            letterSpacing = TextUnit(
                                                1.5f,
                                                TextUnitType.Sp
                                            ),
                                            fontWeight = FontWeight.W500
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = null
                                    )
                                },
                                title = {
                                    Text(
                                        text = stringResource(R.string.confirm_delete_title),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineMedium,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = TextUnit(
                                            0.1f,
                                            TextUnitType.Em
                                        ),
                                        fontWeight = FontWeight.W500
                                    )
                                },
                                text = {
                                    Text(
                                        text = stringResource(R.string.confirm_logout_text),
                                        textAlign = TextAlign.Justify,
                                        style = MaterialTheme.typography.bodyLarge,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = TextUnit(
                                            2f,
                                            TextUnitType.Sp
                                        ),
                                        fontWeight = FontWeight.W400
                                    )
                                },
                            )
                        if (state.openEditEmailDialog)
                            EditEmailDialog(
                                email = state.creatorEmail,
                                emailCode = state.emailCode,
                                onEnterEmail = { onEvent(ProfilePageEvent.SetEmail(it)) },
                                onEnterCode = { onEvent(ProfilePageEvent.SetEmailCode(it)) },
                                onDismissRequest = {
                                    onEvent(
                                        ProfilePageEvent.SetOpenEditEmailDialog(
                                            false
                                        )
                                    )
                                },
                                onConfirmClick = { onEvent(ProfilePageEvent.ChangeEmail) },
                                onGenerateCodeClick = { onEvent(ProfilePageEvent.SetEmailGetCode) }
                            )
                        AnimatedContent(targetState = state.screen,
                            transitionSpec = {
                                if (targetState in arrayOf(
                                        ProfileConfigScreens.MainScreen,
                                        ProfileConfigScreens.Security
                                    ) && initialState != ProfileConfigScreens.MainScreen
                                )
                                    slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                                        spring(stiffness = Spring.StiffnessLow)
                                    ) togetherWith
                                            slideOutHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                                        spring(stiffness = Spring.StiffnessLow)
                                    ) using SizeTransform(clip = false)
                                else slideInHorizontally(
                                    tween(
                                        durationMillis = 500,
                                        delayMillis = 500
                                    )
                                ) { it } + fadeIn(
                                    tween(durationMillis = 500, delayMillis = 500)
                                ) togetherWith
                                        slideOutHorizontally(
                                            tween(
                                                durationMillis = 500,
                                                delayMillis = 500
                                            )
                                        ) { -it } + fadeOut(
                                    tween(durationMillis = 500, delayMillis = 500)
                                ) using SizeTransform(clip = false)
                            }) { currentScreen ->
                            when (currentScreen) {
                                ProfileConfigScreens.MainScreen -> {
                                    Column {
                                        ProfileConfigScreens.entries.minus(
                                            arrayOf(
                                                ProfileConfigScreens.MainScreen,
                                                ProfileConfigScreens.ChangePassword,
                                            )
                                        )
                                            .forEach {
                                                SectionSelectionButton(
                                                    modifier = Modifier
                                                        .padding(8.dp),
                                                    title = {
                                                        Text(
                                                            style = MaterialTheme.typography.titleLarge,
                                                            text = stringResource(it.nameRes),
                                                            textAlign = TextAlign.Start,
                                                            fontWeight = FontWeight.W400,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            letterSpacing = TextUnit(
                                                                0.15f,
                                                                TextUnitType.Em
                                                            )
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        Icon(
                                                            painterResource(R.drawable.forward_ic),
                                                            contentDescription = null
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            painter = painterResource(it.iconRes),
                                                            contentDescription = null
                                                        )
                                                    },
                                                    onClick = {
                                                        onEvent(
                                                            ProfilePageEvent.SetCurrentScreen(
                                                                it
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                        SectionSelectionButton(
                                            modifier = Modifier
                                                .padding(8.dp),
                                            title = {
                                                Text(
                                                    style = MaterialTheme.typography.titleLarge,
                                                    text = stringResource(R.string.sign_out_lbl),
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.W400,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    letterSpacing = TextUnit(
                                                        0.15f,
                                                        TextUnitType.Em
                                                    )
                                                )
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    painterResource(R.drawable.forward_ic),
                                                    contentDescription = null
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.exit_ic),
                                                    contentDescription = null
                                                )
                                            },
                                            onClick = {
                                                onEvent(
                                                    ProfilePageEvent.SetOpenConfirmExitDialog(
                                                        true
                                                    )
                                                )
                                            }
                                        )

                                    }
                                }

                                ProfileConfigScreens.EditInfo -> {
                                    EditInfoContent(
                                        state = state,
                                        onEvent = onEvent
                                    )
                                }

                                ProfileConfigScreens.Security -> {
                                    Column {
                                        SectionSelectionButton(
                                            modifier = Modifier
                                                .padding(8.dp),
                                            title = {
                                                Text(
                                                    style = MaterialTheme.typography.titleLarge,
                                                    text = stringResource(R.string.change_email_address_lbl),
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.W400,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    letterSpacing = TextUnit(
                                                        0.15f,
                                                        TextUnitType.Em
                                                    )
                                                )
                                            },
                                            supportingText = {
                                                if (!state.creatorEmailConfirmed || state.creatorEmail.isEmpty())
                                                    Text(
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        text = stringResource(R.string.email_not_confirmed_warning),
                                                        textAlign = TextAlign.Start,
                                                        color = MaterialTheme.colorScheme.error,
                                                        fontWeight = FontWeight.W400,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        letterSpacing = TextUnit(
                                                            0.1f,
                                                            TextUnitType.Em
                                                        )
                                                    )
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    painterResource(
                                                        if (state.creatorEmailConfirmed && state.creatorEmail.isNotEmpty())
                                                            R.drawable.forward_ic
                                                        else R.drawable.warning_ic
                                                    ),
                                                    contentDescription = null
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.email_ic),
                                                    contentDescription = null
                                                )
                                            },
                                            onClick = {
                                                onEvent(ProfilePageEvent.SetOpenEditEmailDialog(true))
                                            }
                                        )
                                        SectionSelectionButton(
                                            modifier = Modifier
                                                .padding(8.dp),
                                            title = {
                                                Text(
                                                    style = MaterialTheme.typography.titleLarge,
                                                    text = stringResource(ProfileConfigScreens.ChangePassword.nameRes),
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.W400,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    letterSpacing = TextUnit(
                                                        0.15f,
                                                        TextUnitType.Em
                                                    )
                                                )
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    painterResource(R.drawable.forward_ic),
                                                    contentDescription = null
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(ProfileConfigScreens.ChangePassword.iconRes),
                                                    contentDescription = null
                                                )
                                            },
                                            onClick = {
                                                onEvent(
                                                    ProfilePageEvent.SetCurrentScreen(
                                                        ProfileConfigScreens.ChangePassword
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }

                                ProfileConfigScreens.ChangePassword -> {
                                    EditPasswordContent(
                                        password = state.newPassword,
                                        onEnterPassword = { onEvent(ProfilePageEvent.SetPassword(it)) },
                                        repeatPassword = state.repeatPassword,
                                        onEnterRepeatPassword = {
                                            onEvent(
                                                ProfilePageEvent.SetRepeatPassword(
                                                    it
                                                )
                                            )
                                        },
                                        showPassword = state.showPassword,
                                        onSetShowPassword = {
                                            onEvent(
                                                ProfilePageEvent.SetShowPassword(
                                                    it
                                                )
                                            )
                                        },
                                        emailCode = state.emailCode,
                                        onEnterEmailCode = {
                                            onEvent(
                                                ProfilePageEvent.SetEmailCode(
                                                    it
                                                )
                                            )
                                        },
                                        onGenerateCodeClick = { onEvent(ProfilePageEvent.GetEmailCode) }
                                    )
                                }

                                ProfileConfigScreens.Settings -> TODO()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var state by remember {
        mutableStateOf(
            ProfilePageState(
                creator = RecipeResult.Succeed(
                    ProfileRequest(
                        userID = "1231312ijhkjh",
                        nickname = "Some nicknameSome nicknameSome nicknameSome nicknameSome nickname",
                        imageUrl = "",
                        aboutMe = "some about me",
                        login = "",
                        password = "",
                        email = "someEmail@gmail.com",
                        emailConfirmed = false
                    )
                ),
                screen = ProfileConfigScreens.MainScreen,
                creatorName = "Some name to test",
            )
        )
    }
    RecipeSharing_theme(darkTheme = true) {
        Surface {
            CreatorConfigPage(state = state,
                onEvent = {
                    if (it is ProfilePageEvent.SetCreatorName)
                        state = state.copy(
                            creatorName = it.name,
                            isError = it.name.isEmpty()
                        )
                    else if (it is ProfilePageEvent.SetCurrentScreen)
                        state = state.copy(
                            screen = it.screen
                        )
                    else if (it is ProfilePageEvent.SetOpenConfirmExitDialog)
                        state = state.copy(
                            openConfirmExitDialog = it.openDialog
                        )
                    else if (it is ProfilePageEvent.SetPassword)
                        state = state.copy(
                            newPassword = it.newPassword,
                            passwordOk = PasswordChecker.checkPassword(it.newPassword)
                        )
                    else if (it is ProfilePageEvent.SetRepeatPassword)
                        state = state.copy(newPassword = it.repeatPassword)
                    else if (it is ProfilePageEvent.SetShowPassword)
                        state = state.copy(showPassword = it.showPassword)
                    else if (it is ProfilePageEvent.DiscardChanges)
                        state = state.copy(
                            creatorName = "Some random value",
                            creatorLogin = "Some random value",
                            creatorAboutMe = "Some random value",
                            creatorEmail = "Some random value",
                            imageUrl = "",
                            newPassword = "",
                            repeatPassword = "",
                            passwordOk = false,
                            passwordsMatch = false,
                            emailCode = "",
                            isError = false
                        )
                },
                onOpenMenu = {},
                onLogOut = {})
        }
    }
}