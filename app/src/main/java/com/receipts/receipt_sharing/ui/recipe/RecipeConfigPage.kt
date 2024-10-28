package com.receipts.receipt_sharing.ui.recipe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.viewModels.RecipePageEvent
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.domain.response.RecipeResult
import com.receipts.receipt_sharing.ui.ErrorInfoPage
import com.receipts.receipt_sharing.ui.IngredientConfigureDialog
import com.receipts.receipt_sharing.ui.StepConfigureDialog
import com.receipts.receipt_sharing.ui.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeConfigPage(
    modifier: Modifier = Modifier,
    state: RecipePageState,
    onEvent: (RecipePageEvent) -> Unit,
    onOpenMenu: () -> Unit,
    onReloadData: () -> Unit,
    onConfigCompleted: () -> Unit,
    onDiscardChanges : () -> Unit,
    onGoToFilters: () -> Unit
) {
    var openAddIngredientDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var openAddStepDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isEditing by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedIngredient by remember {
        mutableStateOf(Ingredient("", 0, Measure.Gram))
    }
    var selectedStep by remember {
        mutableStateOf(Step("", 0))
    }
    var selectedIndex by remember {
        mutableStateOf(-1)
    }
    val photoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            onEvent(RecipePageEvent.SetImageUrl(uri))
        }
    val refreshState = rememberPullToRefreshState()

    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                title = {
                    when (state.recipe) {
                        is RecipeResult.Downloading -> Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                                .fillMaxWidth()
                                .height(32.dp)
                                .alpha(0.3f)
                                .shimmerEffect()
                        )

                        is RecipeResult.Error -> {
                            ErrorInfoPage(
                                errorInfo = state.recipe.info
                                    ?: stringResource(id = R.string.unknown_error_txt)
                            ) {

                            }
                        }

                        is RecipeResult.Succeed -> Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = state.recipe.data?.recipeName
                                ?: stringResource(R.string.no_recipe),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onDiscardChanges) {
                        Icon(Icons.Default.Clear, contentDescription = "'")
                    }
                })
        },
        bottomBar = {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
                onClick = {
                    onEvent(RecipePageEvent.SaveChanges)
                    onConfigCompleted()
                }) {
                Text(
                    text = stringResource(R.string.save_changes_str),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = refreshState,
            isRefreshing = state.recipe is RecipeResult.Downloading,
            onRefresh = onReloadData,
        ) {
            when (state.recipe) {
                is RecipeResult.Downloading -> {
                    Column {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .align(Alignment.CenterHorizontally)
                                .height(300.dp)
                                .width(300.dp)
                                .shimmerEffect()
                        )
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .align(Alignment.Start)
                                    .height(24.dp)
                                    .fillMaxWidth()
                                    .shimmerEffect()
                            )
                        }
                        LazyVerticalStaggeredGrid(
                            contentPadding = PaddingValues(vertical = 36.dp),
                            columns = StaggeredGridCells.Fixed(2)
                        ) {
                            items(7) {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(12.dp)
                                            .alpha(0.3f)
                                            .clip(CircleShape)
                                            .shimmerEffect()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                            .align(Alignment.CenterVertically)
                                            .height(16.dp)
                                            .fillMaxWidth()
                                            .shimmerEffect()
                                    )
                                }
                            }
                        }
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                                    .align(Alignment.Start)
                                    .height(20.dp)
                                    .fillMaxWidth()
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                is RecipeResult.Error -> {

                    ErrorInfoPage(
                        errorInfo = state.recipe.info
                            ?: stringResource(id = R.string.unknown_error_txt),
                        onReloadPage = onReloadData
                    )
                }

                is RecipeResult.Succeed -> {
                    if (state.recipe.data != null) {
                        if (openAddIngredientDialog)
                            IngredientConfigureDialog(
                                ingredient = selectedIngredient,
                                onDismissRequest = { openAddIngredientDialog = false }) {
                                openAddIngredientDialog = false
                                onEvent(
                                    if (isEditing && selectedIndex != -1) RecipePageEvent.UpdateIngredient(
                                        selectedIndex,
                                        it
                                    )
                                    else RecipePageEvent.AddIngredient(it)
                                )
                                isEditing = false
                                selectedIndex = -1
                            }
                        if (openAddStepDialog)
                            StepConfigureDialog(
                                step = selectedStep,
                                onDismissRequest = { openAddStepDialog = false },
                                onSaveChanges = {
                                    openAddStepDialog = false
                                    onEvent(
                                        if (isEditing && selectedIndex != -1) RecipePageEvent.UpdateStep(
                                            selectedIndex,
                                            it
                                        )
                                        else RecipePageEvent.AddStep(it)
                                    )
                                    isEditing = false
                                    selectedIndex = -1
                                })

                        LazyColumn(
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        ) {
                            item {
                                if (state.recipe.data.imageUrl.isNullOrEmpty())
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable {
                                                photoLauncher.launch(
                                                    PickVisualMediaRequest
                                                        .Builder()
                                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        .build()
                                                )
                                            },
                                        contentScale = ContentScale.Crop,
                                        painter = painterResource(R.drawable.no_image),
                                        contentDescription = ""
                                    )
                                else
                                    AsyncImage(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable {
                                                photoLauncher.launch(
                                                    PickVisualMediaRequest
                                                        .Builder()
                                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                        .build()
                                                )
                                            },
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.recipe.data.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        imageLoader = UnsafeImageLoader.getInstance(),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = "",
                                    )
                            }

                            item {
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
                                        if (!state.filters.data.isNullOrEmpty())
                                            items(state.filters.data) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(
                                                            vertical = 12.dp,
                                                            horizontal = 4.dp
                                                        )
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
                                                            RecipePageEvent.SetFilters(
                                                                state.filters.data.minus(it)
                                                            )
                                                        )
                                                    }) {
                                                        Icon(
                                                            Icons.Default.Clear,
                                                            contentDescription = ""
                                                        )
                                                    }
                                                }
                                            }
                                        else {
                                            item {
                                                Text(
                                                    modifier = Modifier
                                                        .padding(
                                                            vertical = 12.dp,
                                                            horizontal = 8.dp
                                                        )
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
                                }
                            }
                            item {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .padding(
                                            start = 8.dp,
                                            end = 4.dp,
                                            top = 8.dp,
                                            bottom = 12.dp
                                        )
                                        .fillMaxWidth(),
                                    value = state.recipeName,
                                    label = { Text(text = stringResource(R.string.recipe_name_label)) },
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    onValueChange = { onEvent(RecipePageEvent.SetRecipeName(it)) })
                            }
                            item {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .padding(
                                            start = 8.dp,
                                            end = 4.dp,
                                            top = 8.dp,
                                            bottom = 12.dp
                                        )
                                        .fillMaxWidth(),
                                    value = state.recipeDescription,
                                    label = { Text(text = stringResource(R.string.recipe_description_label)) },
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    onValueChange = {
                                        onEvent(
                                            RecipePageEvent.SetRecipeDescription(
                                                it
                                            )
                                        )
                                    })
                            }
                            items(state.ingredients) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(12.dp)
                                            .alpha(0.3f)
                                            .clip(CircleShape)
                                            .background(Color.Gray)
                                    )
                                    IngredientCell(
                                        modifier = Modifier,
                                        ingredient = it
                                    )
                                    Row {
                                        IconButton(onClick = {
                                            selectedIngredient = it
                                            openAddIngredientDialog = true.also {
                                                isEditing = it
                                            }
                                            selectedIndex = state.ingredients.indexOf(it)
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "")
                                        }
                                        IconButton(onClick = {
                                            onEvent(RecipePageEvent.RemoveIngredient(it))
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "")
                                        }
                                    }
                                }
                            }
                            item {
                                OutlinedButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 12.dp),
                                    onClick = {
                                        openAddIngredientDialog = true
                                        isEditing = false
                                        selectedIngredient = Ingredient("", 0, Measure.Litres)
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "")
                                    Text(
                                        style = MaterialTheme.typography.titleMedium,
                                        text = stringResource(id = R.string.add_ingredient_str)
                                    )
                                }
                            }
                            item {
                                EditableStepsRows(modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                    steps = state.steps,
                                    onAddStepClick = {
                                        selectedStep = it
                                        openAddStepDialog = true
                                        isEditing = false
                                    },
                                    onUpdateClick = {
                                        selectedStep = it
                                        selectedIndex = state.steps.indexOf(it)
                                        openAddStepDialog = true
                                        isEditing = true
                                    },
                                    onDeleteClick = {
                                        onEvent(RecipePageEvent.RemoveStep(it))
                                    })
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
private fun ReceiptPagePreview() {
        Surface {
            RecipeConfigPage(modifier = Modifier
                .fillMaxSize(),
                state = RecipePageState(
                    recipe = RecipeResult.Succeed(
                        Recipe(
                            recipeID = "",
                            creatorID = "",
                            imageUrl = "",
                            recipeName = "Extra mayonnaise",
                            description = "Lorem ipsum dolor sit amet",
                            ingredients = listOf(
                                Ingredient("Mayonaise1", 100L, Measure.Millilitres),
                                Ingredient("Mayonaise2", 100L, Measure.Millilitres),
                                Ingredient("Mayonaise3", 100L, Measure.Millilitres),
                                Ingredient("Mayonaise4", 100L, Measure.Millilitres)
                            ),
                            steps = listOf(
                                Step(
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                            "\n" +
                                            "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                    123123123L
                                ),
                                Step(
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                            "\n" +
                                            "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                    123123123L
                                ),
                                Step(
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                            "\n" +
                                            "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                    123123123L
                                ),
                                Step(
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                                            "\n" +
                                            "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.",
                                    123123123L
                                ),
                            )
                        )
                    ),
                    filters = RecipeResult.Succeed(
                        listOf(
                            "FilterOne",
                            "FilterTwo"
                        )
                    )
                ),
                onEvent = {},
                onOpenMenu = {},
                onReloadData = {},
                onConfigCompleted = {},
                onGoToFilters = {},
                onDiscardChanges = {})
        }
    }

