package com.receipts.receipt_sharing.DI

import com.google.gson.GsonBuilder
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.CreatorsRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.FiltersRepositoryImpl
import com.receipts.receipt_sharing.data.repositoriesImpl.RecipesRepositoryImpl
import com.receipts.receipt_sharing.domain.apiServices.RecipesAPIService
import com.receipts.receipt_sharing.domain.apiServices.UnsafeOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://<SERVER_IP>:<SERVER_PORT>/"
val unsafeClient = UnsafeOkHttpClient.getOkHttpClient()
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule{
    @Provides
    @Singleton
    fun provideRecipesAPI() : RecipesAPIService {

        var builder = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(unsafeClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(builder))
            .build()
            .create(RecipesAPIService::class.java)
    }


    @Provides
    @Singleton
    fun provideFiltersRepo(
        api : RecipesAPIService
    ) : FiltersRepositoryImpl {
        return FiltersRepositoryImpl(api)
    }
    @Provides
    @Singleton
    fun provideRecipesRepository(
        api : RecipesAPIService
    ) : RecipesRepositoryImpl {
        return RecipesRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api : RecipesAPIService
    ) : AuthRepositoryImpl {
        return AuthRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCreatorsRepo(
        api : RecipesAPIService
    ) : CreatorsRepositoryImpl {
        return CreatorsRepositoryImpl(api)
    }
}