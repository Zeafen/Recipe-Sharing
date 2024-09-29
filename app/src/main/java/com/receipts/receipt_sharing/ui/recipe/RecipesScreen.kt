package com.receipts.receipt_sharing.ui.recipe

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.recipes.Recipe
import com.receipts.receipt_sharing.data.response.RecipeResult
import com.receipts.receipt_sharing.domain.viewModels.RecipesScreenEvent
import com.receipts.receipt_sharing.ui.ErrorInfoPage
import com.receipts.receipt_sharing.ui.shimmerEffect
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme


@Composable
fun ColumnAmountDropDownMenu(modifier: Modifier = Modifier,
                             onDismissRequest : () -> Unit,
                             onSelectSize : (CellsAmount) -> Unit,
                             expanded : Boolean) {
    DropdownMenu(modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest) {
        CellsAmount.entries.forEach { cell ->
            DropdownMenuItem(text = {
                Text(text = cell.name)
            },
                onClick = {
                    onSelectSize(cell)
                    onDismissRequest()
                })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    modifier : Modifier = Modifier,
    state : RecipesScreenState,
    onEvent : (RecipesScreenEvent) -> Unit,
    onOpenMenu : () -> Unit,
    onGoToAddRecipe : () -> Unit,
    onGoToRecipe : (recipeId : String) -> Unit,
    onGoToFilters : () -> Unit
) {
    var openSelectColumnMenu by rememberSaveable {
        mutableStateOf(false)
    }
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = modifier
            .padding(bottom = 8.dp),
        topBar = {
            TopAppBar(modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                title = {

                    TextField(modifier = Modifier
                        .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        label = { Text(stringResource(R.string.recipe_name_input)) },
                        value = state.searchString, onValueChange = {
                            onEvent(RecipesScreenEvent.SetSearchName(it))
                        })
                },
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(RecipesScreenEvent.SetSearchName(""))
                        }) {
                        Icon(Icons.Default.Clear, contentDescription = "")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                onClick = onGoToAddRecipe
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.recipes is RecipeResult.Downloading,
            onRefresh = {
                onEvent(
                    if (state.favoritesLoaded)
                        RecipesScreenEvent.LoadData
                    else RecipesScreenEvent.LoadFavorites
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        if (state.savedFilters.isNotEmpty())
                            items(state.savedFilters) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp),
                                        text = it,
                                    )
                                    IconButton(onClick = {
                                        onEvent(
                                            RecipesScreenEvent.SetFilters(
                                                state.savedFilters.minus(it)
                                            )
                                        )
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "")
                                    }
                                }
                            }
                        else {
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 8.dp)
                                        .alpha(0.35f),
                                    text = stringResource(id = R.string.no_saved_filters),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    IconButton(onClick = {
                        onGoToFilters()
                    }) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(id = R.drawable.filter_ic),
                            contentDescription = ""
                        )
                    }

                    AnimatedContent(targetState = openSelectColumnMenu) {
                        when (it) {
                            true -> ColumnAmountDropDownMenu(
                                onDismissRequest = { openSelectColumnMenu = false },
                                onSelectSize = {
                                    onEvent(
                                        RecipesScreenEvent.SetCellsAmount(
                                            it
                                        )
                                    )
                                },
                                expanded = openSelectColumnMenu
                            )

                            false -> IconButton(onClick = {
                                openSelectColumnMenu = true
                            }) {
                                Icon(
                                    modifier = Modifier
                                        .size(32.dp),
                                    painter = painterResource(id = R.drawable.cells_amount_ic),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(state.cellsCount.cellsCount),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 24.dp
                ) {
                    when (state.recipes) {
                        is RecipeResult.Downloading -> items(10) {
                            Column {
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

                        is RecipeResult.Error -> {
                            item {
                                ErrorInfoPage(
                                    errorInfo = state.recipes.info
                                        ?: stringResource(id = R.string.unknown_error_txt),
                                    onReloadPage = { onEvent(RecipesScreenEvent.LoadData) }
                                )
                            }
                        }

                        is RecipeResult.Succeed -> {
                            if (state.recipes.data.isNullOrEmpty())
                                item {
                                    Text(
                                        modifier = Modifier
                                            .alpha(0.2f)
                                            .fillMaxSize(),
                                        text = stringResource(R.string.no_items),
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            else
                                items(state.recipes.data) {
                                    RecipeCard(modifier = Modifier
                                        .clickable {
                                            onGoToRecipe(it.recipeID)
                                        }
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                        recipe = it)
                                }
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun RecipesScreenPreview() {
    var opem by remember {
        mutableStateOf(false)
    }
    var state by remember {
        mutableStateOf(RecipesScreenState(
            recipes = RecipeResult.Succeed(
                listOf(
                    Recipe(
                        "",
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
                        emptyList()
                    ),
                    Recipe(
                        "",
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
                        emptyList()
                    ),
                    Recipe(
                        "",
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
                        emptyList()
                    )
                )
            ), cellsCount = CellsAmount.One,
            savedFilters =
            listOf(
            )
        ))
    }

    RecipeSharing_theme(darkTheme = true) {
        Surface {
            if (opem)
                Dialog(onDismissRequest = { opem = false }) {
                    Text(text = "Here i go")
                }
            RecipesScreen(state = state,
                onEvent = {
                    if (it is RecipesScreenEvent.SetSearchName)
                        state = state.copy(searchString = it.receiptName)
                    else if(it is RecipesScreenEvent.SetCellsAmount)
                        state = state.copy(cellsCount = it.cellsAmount)
                    else if(it is RecipesScreenEvent.SetFilters)
                        state = state.copy(savedFilters = it.filters)
                },
                onOpenMenu = {},
                onGoToAddRecipe = {},
                onGoToRecipe = { opem = true },
                onGoToFilters = {})
        }
    }
}
