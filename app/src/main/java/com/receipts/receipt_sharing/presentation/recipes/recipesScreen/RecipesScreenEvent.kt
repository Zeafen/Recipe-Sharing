package com.receipts.receipt_sharing.presentation.recipes.recipesScreen

import com.receipts.receipt_sharing.domain.filters.RecipeOrdering
import com.receipts.receipt_sharing.presentation.CellsAmount
import com.receipts.receipt_sharing.presentation.PageSizes

sealed interface RecipesScreenEvent {
    data object LoadData : RecipesScreenEvent
    data class SetOpenFiltersPage(val openDialog: Boolean) : RecipesScreenEvent
    data class SetLoadDataType(val dataType: RecipesLoadedDataType) : RecipesScreenEvent
    data class SetSearchName(val recipeName: String) : RecipesScreenEvent
    data class SetCellsAmount(val cellsAmount: CellsAmount) : RecipesScreenEvent
    data class SetFilters(val filters: List<String>) : RecipesScreenEvent
    data class SetIngredients(val ingredients: List<String>) : RecipesScreenEvent
    data class SetOrdering(val ordering: RecipeOrdering?) : RecipesScreenEvent
    data object LoadFilters : RecipesScreenEvent

    data class SetExpandFiltersTab(val expandTab : Boolean) : RecipesScreenEvent
    data class SetOpenSelectColumnMenu(val openMenu: Boolean) : RecipesScreenEvent
    data class SetOpenSelectOrderingMenu(val openMenu: Boolean) : RecipesScreenEvent
    data class SetOpenSearch(val openSearch: Boolean) : RecipesScreenEvent
    data class SetCurrentPage(val currentPage: Int) : RecipesScreenEvent
    data class SetPageSize(val pageSizes: PageSizes) : RecipesScreenEvent
}