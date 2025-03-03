package com.receipts.receipt_sharing.DI

import IRecipesRepository
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.GsonBuilder
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.CreatorsRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.FiltersRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.RecipesRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.ReviewsRepositoryImpl
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.apiServices.UnsafeOkHttpClient
import com.receipts.receipt_sharing.domain.repositories.IAuthRepository
import com.receipts.receipt_sharing.domain.repositories.ICreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.IFiltersRepository
import com.receipts.receipt_sharing.domain.repositories.IReviewsRepository
import com.receipts.receipt_sharing.presentation.auth.AuthViewModel
import com.receipts.receipt_sharing.presentation.creators.CreatorPageViewModel
import com.receipts.receipt_sharing.presentation.creators.CreatorsScreenViewModel
import com.receipts.receipt_sharing.presentation.creators.ProfileViewModel
import com.receipts.receipt_sharing.presentation.recipes.RecipePageViewModel
import com.receipts.receipt_sharing.presentation.recipes.RecipesScreenViewModel
import com.receipts.receipt_sharing.presentation.reviews.ReviewPageViewModel
import com.receipts.receipt_sharing.presentation.reviews.ReviewsScreenViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

interface IAppModule {
    val BASE_URL: String
    val apiService: RecipesAPIService
    val recipesRepo: IRecipesRepository
    val authDataStoreRepo: AuthDataStoreRepository
    val authRepo: IAuthRepository
    val creatorsRepo: ICreatorsRepository
    val filtersRepo: IFiltersRepository
    val reviewsRepo: IReviewsRepository

    val authVMFactory: ViewModelProvider.Factory
    val creatorsScreenVMFactory: ViewModelProvider.Factory
    val creatorPageVMFactory: ViewModelProvider.Factory
    val profilePageVMFactory : ViewModelProvider.Factory
    val recipesScreenVMFactory: ViewModelProvider.Factory
    val recipePageVMFactory: ViewModelProvider.Factory
    val reviewsVMFactory : ViewModelProvider.Factory
    val reviewsScreenVMFactory : ViewModelProvider.Factory
    val reviewPageVMFactory : ViewModelProvider.Factory
}

class ManualAppModule(
    private val applicationContext: Context
) : IAppModule {
    override val BASE_URL: String
        get() = ""

    override val apiService: RecipesAPIService
        get() {
            var builder = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(UnsafeOkHttpClient.getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(builder))
                .build()
                .create(RecipesAPIService::class.java)
        }

    override val recipesRepo: IRecipesRepository by lazy {
        RecipesRepositoryImpl(apiService)
    }
    override val authDataStoreRepo: AuthDataStoreRepository
        get() = AuthDataStoreRepository.get()
    override val authRepo: IAuthRepository by lazy {
        AuthRepositoryImpl(apiService)
    }
    override val creatorsRepo: ICreatorsRepository by lazy {
        CreatorsRepositoryImpl(apiService)
    }
    override val filtersRepo: IFiltersRepository by lazy {
        FiltersRepositoryImpl(apiService)
    }
    override val reviewsRepo: IReviewsRepository by lazy {
        ReviewsRepositoryImpl(apiService)
    }



    override val reviewsVMFactory: ViewModelProvider.Factory by lazy{
        viewModelFactory {
            initializer {
                ReviewsScreenViewModel(reviewsRepo, creatorsRepo)
            }
        }
    }
    override val authVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                AuthViewModel(authRepo, creatorsRepo)
            }
        }
    }
    override val creatorsScreenVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                CreatorsScreenViewModel(creatorsRepo)
            }
        }
    }
    override val creatorPageVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                CreatorPageViewModel(recipesRepo, creatorsRepo)
            }
        }
    }
    override val recipesScreenVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                RecipesScreenViewModel(recipesRepo, filtersRepo)
            }
        }
    }
    override val recipePageVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                RecipePageViewModel(recipesRepo, filtersRepo, reviewsRepo, creatorsRepo)
            }
        }
    }
    override val reviewsScreenVMFactory: ViewModelProvider.Factory by lazy{
        viewModelFactory {
            initializer {
                ReviewsScreenViewModel(reviewsRepo, creatorsRepo)
            }
        }
    }
    override val reviewPageVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                ReviewPageViewModel(creatorsRepo, recipesRepo, reviewsRepo)
            }
        }
    }
    override val profilePageVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                ProfileViewModel(recipesRepo, creatorsRepo)
            }
        }
    }

}