package com.receipts.receipt_sharing.presentation.recipes.recipesScreen

import com.receipts.receipt_sharing.domain.filters.RecipeOrdering
import com.receipts.receipt_sharing.domain.recipes.Recipe
import com.receipts.receipt_sharing.domain.response.ApiResult
import com.receipts.receipt_sharing.presentation.CellsAmount
import com.receipts.receipt_sharing.presentation.PageSizes

data class RecipesScreenState(
    val recipes : ApiResult<List<Recipe>> = ApiResult.Downloading(),

    //filtering
    val loadedFilters : ApiResult<Map<String, List<String>>> = ApiResult.Downloading(),
    val searchedFilters : List<String> = emptyList(),
    val searchedIngredients : List<String> = emptyList(),
    val recipeOrdering: RecipeOrdering? = null,
    val ascending: Boolean = true,
    val searchString : String = "",

    //paging
    val cellsCount: CellsAmount = CellsAmount.One,
    val currentPage : Int = 1,
    val maxPages : Int = 1,
    val pageSize : PageSizes = PageSizes.Standard,

    //UI
    val recipesLoadedDataType : RecipesLoadedDataType = RecipesLoadedDataType.All,
    val openSearch : Boolean = false,
    val openSelectColumnMenu : Boolean = false,
    val openSelectOrderingMenu : Boolean = false,
    val expandFiltersTab : Boolean = false,
    val openFiltersPage : Boolean = false
)