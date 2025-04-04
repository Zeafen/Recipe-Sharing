package com.receipts.receipt_sharing.DI

import RecipesRepository
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.GsonBuilder
import com.receipts.receipt_sharing.data.helpers.UnsafeOkHttpClient
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.CreatorsRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.FiltersRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.RecipesRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.ReviewsRepositoryImpl
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.repositories.AuthRepository
import com.receipts.receipt_sharing.domain.repositories.CreatorsRepository
import com.receipts.receipt_sharing.domain.repositories.FiltersRepository
import com.receipts.receipt_sharing.domain.repositories.ReviewsRepository
import com.receipts.receipt_sharing.presentation.auth.AuthViewModel
import com.receipts.receipt_sharing.presentation.creators.creatorPage.CreatorPageViewModel
import com.receipts.receipt_sharing.presentation.creators.creatorsScreen.CreatorsScreenViewModel
import com.receipts.receipt_sharing.presentation.creators.profile.ProfileViewModel
import com.receipts.receipt_sharing.presentation.home.HomePageViewModel
import com.receipts.receipt_sharing.presentation.recipes.recipePage.RecipePageViewModel
import com.receipts.receipt_sharing.presentation.recipes.recipesScreen.RecipesScreenViewModel
import com.receipts.receipt_sharing.presentation.reviews.reviewPage.ReviewPageViewModel
import com.receipts.receipt_sharing.presentation.reviews.reviewsScreen.ReviewsScreenViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class ManualAppModule(
    private val applicationContext: Context
) : AppModule {
    override val BASE_URL: String
        get() = "https://192.168.148.103:7129/"

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

    override val recipesRepo: RecipesRepository by lazy {
        RecipesRepositoryImpl(apiService)
    }
    override val authDataStoreRepo: AuthDataStoreRepository
        get() = AuthDataStoreRepository.get()
    override val authRepo: AuthRepository by lazy {
        AuthRepositoryImpl(apiService)
    }
    override val creatorsRepo: CreatorsRepository by lazy {
        CreatorsRepositoryImpl(apiService)
    }
    override val filtersRepo: FiltersRepository by lazy {
        FiltersRepositoryImpl(apiService)
    }
    override val reviewsRepo: ReviewsRepository by lazy {
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

    override val homePageVMFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            initializer {
                HomePageViewModel(recipesRepo, creatorsRepo)
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
                ReviewPageViewModel(recipesRepo, reviewsRepo)
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