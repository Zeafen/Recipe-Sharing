package com.receipts.receipt_sharing.ui.recipe

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.recipes.Step
import com.receipts.receipt_sharing.ui.theme.RecipeSharing_theme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StepRow(modifier : Modifier = Modifier,
            stepOrder : Int,
            step: Step
) {
    var openStepsState by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = modifier,
        onClick = { openStepsState = !openStepsState }
    ) {
        AnimatedContent(
            targetState = openStepsState,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) togetherWith
                        fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (openStepsState)
                                keyframes {
                                    IntSize(targetSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                } else
                                keyframes {
                                    IntSize(initialSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                        }
            }) { targetExpanded ->
            if (!targetExpanded)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.step_order, stepOrder),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier
                            .alpha(0.5f),
                        text = "${step.duration} min",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            else {
                Column{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(R.string.step_order, stepOrder),
                            style = MaterialTheme.typography.titleMedium)
                        Text(modifier = Modifier
                            .alpha(0.5f),
                            text = "${step.duration} min",
                            style = MaterialTheme.typography.titleMedium)
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .alpha(0.7f)

                    ) {
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
@Composable
fun StepsRows(modifier : Modifier = Modifier,
              steps : List<Step>
){
    var showSteps by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = modifier,
        onClick = { showSteps = !showSteps }
    ) {
        AnimatedContent(targetState = showSteps,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) togetherWith
                        fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (showSteps)
                                keyframes {
                                    IntSize(targetSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                }else {
                                keyframes {
                                    IntSize(initialSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                            }
                        }
            }) { stepsShown ->
            if(!stepsShown)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.instruction_string, steps.size),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier
                            .alpha(0.5f),
                        text = "${steps.sumOf { s -> s.duration }} min",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            else{
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.instruction_string, steps.size),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier
                                .alpha(0.5f),
                            text = "${steps.sumOf { s -> s.duration }} min",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    repeat(steps.size) {
                        StepRow(modifier = Modifier,
                            stepOrder = it, step = steps[it])
                    }
                }
            }
        }
    }
}

@Composable
fun EditableStepsRows(modifier: Modifier = Modifier,
                      steps : List<Step>,
                      onAddStepClick : (Step) -> Unit,
                      onUpdateClick : (Step) -> Unit,
                      onDeleteClick : (Step) -> Unit) {
    var showSteps by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = modifier,
        onClick = { showSteps = !showSteps }
    ) {
        AnimatedContent(targetState = showSteps,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) togetherWith
                        fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (showSteps)
                                keyframes {
                                    IntSize(targetSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                }else {
                                keyframes {
                                    IntSize(initialSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                            }
                        }
            }) { stepsShown ->
            if(!stepsShown)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.instruction_string, steps.size),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier
                            .alpha(0.5f),
                        text = "${steps.sumOf { s -> s.duration }} min",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            else{
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.instruction_string, steps.size),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier
                                .alpha(0.5f),
                            text = "${steps.sumOf { s -> s.duration }} min",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    repeat(steps.size) {
                        Row {
                            StepRow(modifier = Modifier
                                .weight(1f)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {

                                        }
                                    )
                                },
                                stepOrder = it+1, step = steps[it]
                            )

                            IconButton(onClick = { onUpdateClick(steps[it]) }) {
                                Icon(Icons.Default.Edit, contentDescription = "")
                            }
                            IconButton(onClick = { onDeleteClick(steps[it]) }) {
                                Icon(Icons.Default.Delete, contentDescription = "")
                            }
                        }
                    }
                    OutlinedButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                        onClick = { onAddStepClick(Step("", 0)) }) {
                        Icon(Icons.Default.Add, contentDescription = "")
                        Text(text = stringResource(id = R.string.add_step_str))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun StepsRowsPreview(){
    var recipe by remember {
        mutableStateOf(
            Recipe(
            "",
            "",
            "",
            "New name",
            "Description",
            emptyList(),
            listOf(
                Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                        "\n" +
                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L),
                Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                        "\n" +
                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L),
                Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                        "\n" +
                        "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L)
            )
        )
        )
    }
    RecipeSharing_theme {

    EditableStepsRows(steps = recipe.steps,
        onAddStepClick = {step ->
            recipe = recipe.copy(
                steps = recipe.steps.plus(step)
            )
        },
        onDeleteClick = {
            recipe = recipe.copy(
                steps = recipe.steps.minus(it)
            )
        },
        onUpdateClick = {})
    }
}

@Composable
@Preview
private fun StepsPreview(){
    StepRow(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp)),
        stepOrder = 1,
        step = Step("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse sit amet est varius, tempor tortor non, pellentesque mi. Praesent accumsan facilisis urna nec semper. Proin gravida consectetur augue. Nullam pharetra nulla at malesuada consequat. Donec eu tortor vitae risus laoreet mollis nec in ipsum. Donec sem erat, rhoncus a iaculis at, accumsan eget nisl. Nulla hendrerit dui in quam rutrum, id ultricies urna facilisis. Fusce urna augue, maximus at tortor pellentesque, laoreet auctor tortor. Maecenas ut eros enim. Donec faucibus venenatis semper. Pellentesque laoreet metus blandit arcu venenatis auctor ac non arcu.\n" +
                "\n" +
                "Phasellus nulla leo, condimentum in est et, ornare tincidunt neque. Morbi lectus velit, cursus quis pharetra sed, semper rhoncus felis. Pellentesque volutpat ipsum vitae mattis sodales. Proin mattis nulla velit, ac venenatis nisi euismod ut. Sed non imperdiet neque. Sed lacinia libero erat. Vestibulum id pellentesque tellus, at suscipit nulla. Duis ut erat interdum, laoreet nibh ut, lobortis est.", 123123123L)
    )
}