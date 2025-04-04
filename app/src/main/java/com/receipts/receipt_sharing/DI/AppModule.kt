package com.receipts.receipt_sharing.DI

import RecipesRepository
import androidx.lifecycle.ViewModelProvider
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.repositories.AuthRepository
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.FiltersRepository
import com.receipts.receipt_sharing.domain.repositories.ReviewsRepository

interface AppModule {
    val BASE_URL: String
    val apiService: RecipesAPIService
    val recipesRepo: RecipesRepository
    val authDataStoreRepo: AuthDataStoreRepository
    val authRepo: AuthRepository
    val creatorsRepo: CreatorsRepository
    val filtersRepo: FiltersRepository
    val reviewsRepo: ReviewsRepository

    val authVMFactory: ViewModelProvider.Factory
    val homePageVMFactory : ViewModelProvider.Factory
    val creatorsScreenVMFactory: ViewModelProvider.Factory
    val creatorPageVMFactory: ViewModelProvider.Factory
    val profilePageVMFactory : ViewModelProvider.Factory
    val recipesScreenVMFactory: ViewModelProvider.Factory
    val recipePageVMFactory: ViewModelProvider.Factory
    val reviewsVMFactory : ViewModelProvider.Factory
    val reviewsScreenVMFactory : ViewModelProvider.Factory
    val reviewPageVMFactory : ViewModelProvider.Factory
}