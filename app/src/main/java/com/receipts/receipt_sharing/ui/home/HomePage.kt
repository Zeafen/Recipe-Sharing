package com.receipts.receipt_sharing.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.receipts.receipt_sharing.domain.creators.CreatorRequest
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.home.HomePageEvent
import com.receipts.receipt_sharing.presentation.home.HomePageState
import com.receipts.receipt_sharing.ui.creators.CreatorCell
import com.receipts.receipt_sharing.ui.effects.shimmerEffect
import com.receipts.receipt_sharing.ui.recipe.RecipeCard
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

/**
 * Composes home page
 * @param modifier Modifier applied to HomePage
 * @param state state object used to control layout
 *  @param onEvent called when user interacts with ui
 *  @param onGoToRecipes called when user clicks on "More" button in recipes list
 *  @param onGoToRecipe called when user clicks on recipe card
 *  @param onGoToCreators called when user clicks on "More" button in creators list
 *  @param onOpenMenu called when user click "Menu" navigation button
 *  @param onGoToCreator called when user clicks on creator card
 *  @param onGoToProfile called when uer clicks on "profile" action button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    state: HomePageState,
    onEvent: (HomePageEvent) -> Unit,
    onGoToRecipes: () -> Unit,
    onGoToRecipe: (String) -> Unit,
    onGoToCreators: () -> Unit,
    onGoToCreator: (String) -> Unit,
    onOpenMenu: () -> Unit,
    onGoToProfile : () -> Unit
) {
    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(modifier = Modifier
                .clip(RoundedCornerShape(bottomStartPercent = 40, bottomEndPercent = 40)),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.secondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                },
                actions = {
                    FilledIconButton(
                        onClick = onGoToProfile,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.person_ic),
                            contentDescription = ""
                        )
                    }
                },
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.welcome_header, state.userName),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 2,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(0.6f),
                            text = stringResource(R.string.top_publishers_lbl),
                            style = MaterialTheme.typography.headlineMedium,
                            letterSpacing = TextUnit(
                                0.15f,
                                TextUnitType.Em
                            ),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.W400,
                        )
                        AssistChip(
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            onClick = onGoToCreators,
                            label = {
                                Text(text = stringResource(R.string.more_btn_text))
                            },
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp),
                                    painter = painterResource(R.drawable.forward_ic),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    when (state.topPublishers) {
                        is ApiResult.Downloading -> LazyRow {
                            items(5) {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp, horizontal = 8.dp)
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .alpha(0.5f)
                                            .shimmerEffect()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .width(120.dp)
                                            .height(20.dp)
                                            .shimmerEffect()
                                    )
                                }
                            }
                        }

                        is ApiResult.Error -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.onErrorContainer
                                ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        text = stringResource(R.string.error_info_title),
                                        textAlign = TextAlign.Start,
                                        style = MaterialTheme.typography.titleLarge,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = TextUnit(
                                            0.1f,
                                            TextUnitType.Em
                                        ),
                                        fontWeight = FontWeight.W500
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(top = 12.dp, start = 8.dp, end = 8.dp),
                                        text = state.topPublishers.info
                                            ?: stringResource(R.string.unknown_error_txt),
                                        textAlign = TextAlign.Justify,
                                        style = MaterialTheme.typography.bodyLarge,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = TextUnit(
                                            2f,
                                            TextUnitType.Sp
                                        ),
                                        fontWeight = FontWeight.W400
                                    )
                                    AssistChip(
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                        onClick = { onEvent(HomePageEvent.LoadPublishers) },
                                        label = {
                                            Text(text = stringResource(R.string.reload_page_btn_txt))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier
                                                    .padding(end = 8.dp),
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        is ApiResult.Succeed -> {
                            if (state.topPublishers.data.isNullOrEmpty())
                                Text(
                                    modifier = Modifier
                                        .alpha(0.5f)
                                        .padding(horizontal = 12.dp),
                                    text = stringResource(R.string.no_creators),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    letterSpacing = TextUnit(
                                        1.5f,
                                        TextUnitType.Sp
                                    ),
                                    fontWeight = FontWeight.W400
                                )
                            else LazyRow {
                                items(state.topPublishers.data) { creator ->
                                    CreatorCell(
                                        modifier = Modifier
                                            .width(192.dp)
                                            .padding(horizontal = 8.dp)
                                            .clickable {
                                                onGoToCreator(creator.userID)
                                            },
                                        creator = creator
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(0.6f),
                            text = stringResource(R.string.popular_recipes_lbl),
                            style = MaterialTheme.typography.headlineMedium,
                            letterSpacing = TextUnit(
                                0.15f,
                                TextUnitType.Em
                            ),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.W400,
                        )
                        AssistChip(
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            onClick = onGoToRecipes,
                            label = {
                                Text(text = stringResource(R.string.more_btn_text))
                            },
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp),
                                    painter = painterResource(R.drawable.forward_ic),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                    when (state.popularRecipes) {
                        is ApiResult.Downloading -> LazyRow {
                            items(5) {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(200.dp)
                                            .padding(8.dp)
                                            .shimmerEffect()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .height(28.dp)
                                            .width(200.dp)
                                            .padding(
                                                start = 8.dp,
                                                end = 8.dp,
                                                top = 12.dp,
                                                bottom = 4.dp
                                            )
                                            .shimmerEffect()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .height(64.dp)
                                            .width(200.dp)
                                            .padding(8.dp)
                                            .shimmerEffect(),
                                    )
                                }
                            }
                        }

                        is ApiResult.Error -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.onErrorContainer
                                ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        text = stringResource(R.string.error_info_title),
                                        textAlign = TextAlign.Start,
                                        style = MaterialTheme.typography.titleLarge,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = TextUnit(
                                            0.1f,
                                            TextUnitType.Em
                                        ),
                                        fontWeight = FontWeight.W500
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(top = 12.dp, start = 8.dp, end = 8.dp),
                                        text = state.topPublishers.info
                                            ?: stringResource(R.string.unknown_error_txt),
                                        textAlign = TextAlign.Justify,
                                        style = MaterialTheme.typography.bodyLarge,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = TextUnit(
                                            2f,
                                            TextUnitType.Sp
                                        ),
                                        fontWeight = FontWeight.W400
                                    )
                                    AssistChip(
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                        onClick = { onEvent(HomePageEvent.LoadPopulars) },
                                        label = {
                                            Text(text = stringResource(R.string.reload_page_btn_txt))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier
                                                    .padding(end = 8.dp),
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        is ApiResult.Succeed -> {
                            if (state.popularRecipes.data.isNullOrEmpty())
                                Text(
                                    modifier = Modifier
                                        .alpha(0.5f)
                                        .padding(horizontal = 12.dp),
                                    text = stringResource(R.string.no_recipes),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    letterSpacing = TextUnit(
                                        1.5f,
                                        TextUnitType.Sp
                                    ),
                                    fontWeight = FontWeight.W400
                                )
                            else LazyRow {
                                items(state.popularRecipes.data) { recipe ->
                                    RecipeCard(
                                        modifier = Modifier
                                            .width(256.dp)
                                            .padding(horizontal = 12.dp)
                                            .clickable {
                                                onGoToRecipe(recipe.recipeID)
                                            },
                                        recipe = recipe
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(vertical = 24.dp, horizontal = 8.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(CircleShape),
                        thickness = 4.dp
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(CircleShape),
                        thickness = 4.dp
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        text = stringResource(R.string.recent_recipes_lbl),
                        style = MaterialTheme.typography.headlineMedium,
                        letterSpacing = TextUnit(
                            0.15f,
                            TextUnitType.Em
                        ),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.W400,
                    )
                }
            }
            when (state.recentRecipes) {
                is ApiResult.Downloading -> {
                    items(5) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(200.dp)
                                    .padding(8.dp)
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, top = 12.dp, bottom = 4.dp)
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .height(64.dp)
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp, horizontal = 8.dp)
                                    .shimmerEffect(),
                            )
                        }
                    }
                }

                is ApiResult.Error -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.onErrorContainer
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = stringResource(R.string.error_info_title),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.titleLarge,
                                    overflow = TextOverflow.Ellipsis,
                                    letterSpacing = TextUnit(
                                        0.1f,
                                        TextUnitType.Em
                                    ),
                                    fontWeight = FontWeight.W500
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(top = 12.dp, start = 8.dp, end = 8.dp),
                                    text = state.topPublishers.info
                                        ?: stringResource(R.string.unknown_error_txt),
                                    textAlign = TextAlign.Justify,
                                    style = MaterialTheme.typography.bodyLarge,
                                    overflow = TextOverflow.Ellipsis,
                                    letterSpacing = TextUnit(
                                        2f,
                                        TextUnitType.Sp
                                    ),
                                    fontWeight = FontWeight.W400
                                )
                                AssistChip(
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                    onClick = { onEvent(HomePageEvent.LoadRecents) },
                                    label = {
                                        Text(text = stringResource(R.string.reload_page_btn_txt))
                                    },
                                    leadingIcon = {
                                        Icon(
                                            modifier = Modifier
                                                .padding(end = 8.dp),
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                is ApiResult.Succeed -> {
                    if (state.recentRecipes.data.isNullOrEmpty())
                        item {
                            Text(
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .padding(horizontal = 12.dp),
                                text = stringResource(R.string.no_creators),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                letterSpacing = TextUnit(
                                    1.5f,
                                    TextUnitType.Sp
                                ),
                                fontWeight = FontWeight.W400
                            )
                        }
                    else {
                        items(state.recentRecipes.data) { recipe ->
                            RecipeCard(
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                                    .clickable {
                                        onGoToRecipe(recipe.recipeID)
                                    },
                                recipe = recipe
                            )
                        }
                        item {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp, start = 12.dp, end = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                onClick = onGoToRecipes,
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp),
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                                Text(text = stringResource(R.string.more_btn_text))
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
    RecipeSharing_theme {
        Surface {
            HomePage(
                state = HomePageState(
                    userName = "ksjdshdlksahdasjhdksahdksajhdkashdksjahdskahd",
                    topPublishers = ApiResult.Succeed(
                        listOf(
                            CreatorRequest(
                                "1",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "2",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "3",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "4",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "5",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "6",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            ),
                            CreatorRequest(
                                "7",
                                "Very Very Very Very Very Very Very Very Very Very Very long name",
                                "",
                                ""
                            )
                        )
                    ),
                    popularRecipes = ApiResult.Succeed(
                        listOf(
                            Recipe(
                                "1",
                                "",
                                "",
                                "very long name created to test its length",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                        "\n" +
                                        "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                        "\n" +
                                        "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                        "\n" +
                                        "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                        "\n" +
                                        "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                                emptyList(),
                                emptyList(),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            ),
                            Recipe(
                                "2",
                                "",
                                "",
                                "Name",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                        "\n" +
                                        "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                        "\n" +
                                        "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                        "\n" +
                                        "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                        "\n" +
                                        "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                                emptyList(),
                                emptyList(),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            ),
                            Recipe(
                                "3",
                                "",
                                "",
                                "Name",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                        "\n" +
                                        "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                        "\n" +
                                        "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                        "\n" +
                                        "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                        "\n" +
                                        "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                                emptyList(),
                                emptyList(),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            )
                        )
                    ),
                    recentRecipes = ApiResult.Succeed(
                        listOf(
                            Recipe(
                                "1",
                                "",
                                "",
                                "very long name created to test its length",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                        "\n" +
                                        "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                        "\n" +
                                        "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                        "\n" +
                                        "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                        "\n" +
                                        "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                                emptyList(),
                                emptyList(),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            ),
                            Recipe(
                                "2",
                                "",
                                "",
                                "Name",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                        "\n" +
                                        "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                        "\n" +
                                        "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                        "\n" +
                                        "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                        "\n" +
                                        "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                                emptyList(),
                                emptyList(),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            ),
                            Recipe(
                                "3",
                                "",
                                "",
                                "Name",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam dapibus ex sit amet purus posuere, id dapibus neque rhoncus. Donec sit amet tristique libero. Vestibulum pulvinar congue tellus, ac maximus velit maximus sit amet. Nunc cursus lectus eu sem vestibulum, ac fringilla est commodo. Cras molestie pharetra dui. Integer feugiat nec tellus eu pretium. Aenean ex nunc, suscipit ac lobortis at, laoreet vel nunc. Cras neque nunc, rutrum in scelerisque ac, fringilla ut nunc. Sed vitae est eu ipsum mollis venenatis in non sem. Praesent ullamcorper imperdiet dignissim. Donec vulputate pharetra venenatis. Mauris pellentesque consectetur arcu.\n" +
                                        "\n" +
                                        "Cras cursus ante eros, a venenatis lacus gravida ut. Sed a mi erat. Nulla ullamcorper felis sagittis felis porttitor congue. Morbi finibus neque quis sodales viverra. Vestibulum a est ac leo aliquam lacinia. Phasellus tempor eros rhoncus, facilisis libero ac, vestibulum nisi. Sed malesuada felis mi, et porttitor orci porta sed. In vitae dapibus tellus. Fusce sit amet hendrerit nibh. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam lacinia tellus mollis metus dapibus volutpat. Vestibulum nec congue turpis, et egestas velit. Morbi et risus vel sem hendrerit sagittis in at erat.\n" +
                                        "\n" +
                                        "Nulla pharetra mauris in urna ornare, sed volutpat tellus rutrum. Suspendisse vel tincidunt tellus. Cras dapibus lectus tristique sapien tristique, eu egestas massa varius. Morbi ut elementum mauris. Suspendisse mattis faucibus nulla sed sollicitudin. Integer eget orci euismod, congue metus id, aliquam diam. Nulla hendrerit efficitur erat ac varius. Pellentesque non purus nibh. Nunc vel bibendum nisl. Nam eget ligula vitae est viverra mollis. Nullam viverra ipsum non ligula ultricies, in aliquam mi rhoncus. Nulla urna enim, tempus sed porta vel, convallis quis turpis. Aliquam vitae porttitor lacus, ut semper purus. Sed placerat lectus nunc, eget tincidunt felis mollis non. Cras enim purus, lobortis vel massa vel, egestas aliquam leo.\n" +
                                        "\n" +
                                        "Phasellus bibendum, augue eu condimentum ornare, nunc enim auctor nisl, non tristique sapien erat et augue. Maecenas varius dui ac tellus consequat, eget feugiat augue malesuada. Nullam in nibh interdum erat aliquam posuere nec sed libero. Mauris sed nibh porta, consequat turpis quis, finibus velit. Proin pharetra mollis facilisis. Morbi egestas, nisi a lacinia scelerisque, ligula lorem aliquam lorem, eu pellentesque lorem lorem et lacus. Aenean nec efficitur libero, et blandit odio.\n" +
                                        "\n" +
                                        "Vivamus eu neque pharetra, malesuada leo elementum, eleifend velit. Nulla faucibus rutrum felis a condimentum. Vivamus eu nisl eget dolor pretium elementum. Ut semper lacus odio, a varius felis luctus nec. Aliquam ac libero tincidunt arcu sollicitudin ultricies non sed velit. Cras vitae finibus purus. Maecenas facilisis, velit ut tempus accumsan, arcu risus lobortis erat, id vehicula mauris ligula id enim.",
                                emptyList(),
                                emptyList(),
                                reviewsCount = 100_000_000,
                                currentRating = 0f,
                                viewsCount = 0L
                            )
                        )
                    )
                ),
                onGoToRecipes = {},
                onGoToCreators = {},
                onOpenMenu = {},
                onEvent = {},
                onGoToRecipe = {},
                onGoToCreator = {},
                onGoToProfile = {}
            )
        }
    }
}