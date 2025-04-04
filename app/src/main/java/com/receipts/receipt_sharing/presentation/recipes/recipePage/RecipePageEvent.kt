package com.receipts.receipt_sharing.presentation.recipes.recipePage

import android.net.Uri
import com.receipts.receipt_sharing.domain.recipes.Ingredient
import com.receipts.receipt_sharing.domain.recipes.Measure
import com.receipts.receipt_sharing.domain.recipes.Step

sealed interface RecipePageEvent {
    //Recipe
    data class LoadRecipe(val recipeID: String) : RecipePageEvent
    data object LoadAllFilters : RecipePageEvent
    data object EditRecord : RecipePageEvent
    data object DiscardChanges : RecipePageEvent
    data object DeleteRecipe : RecipePageEvent
    data class SetRecipeName(val receiptName: String) : RecipePageEvent
    data class SetRecipeDescription(val receiptDescription: String) : RecipePageEvent
    data object ChangeIsFavorite : RecipePageEvent
    data object InitializeRecipe : RecipePageEvent
    data class SetFilters(val filters: List<String>) : RecipePageEvent

    //Ingredients
    data class AddIngredient(val ingredient: Ingredient) : RecipePageEvent
    data class UpdateIngredient(val index: Int, val ingredient: Ingredient) : RecipePageEvent
    data class RemoveIngredient(val ingredient: Ingredient) : RecipePageEvent
    data class SetSelectedIngredient(
        val ingredient: Ingredient = Ingredient(
            "",
            0f,
            Measure.Gram
        )
    ) : RecipePageEvent

    //Steps
    data class SetImageUrl(val imageUri: Uri?) : RecipePageEvent
    data class AddStep(val step: Step) : RecipePageEvent
    data class RemoveStep(val step: Step) : RecipePageEvent
    data class UpdateStep(val index: Int, val step: Step) : RecipePageEvent
    data class SetSelectedStep(val step: Step = Step("", 0L)) : RecipePageEvent

    //Page operations
    data object SaveChanges : RecipePageEvent
    data class SetOpenIngredientConfigDialog(val openDialog: Boolean) : RecipePageEvent
    data class SetOpenStepConfigDialog(val openDialog: Boolean) : RecipePageEvent
    data class SetOpenFiltersPage(val openDialog: Boolean) : RecipePageEvent
    data object OpenConfirmDeleteDialog : RecipePageEvent
    data object CloseDialogs : RecipePageEvent
    data class SetSelectedRecipeTabIndex(val recipeTabIndex: Int) : RecipePageEvent
    data class SetIsError(val isError: Boolean) : RecipePageEvent
}