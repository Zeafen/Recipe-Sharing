package com.receipts

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.receipts.receipt_sharing.DI.IAppModule
import com.receipts.receipt_sharing.DI.ManualAppModule
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.helpers.FileHelper
import com.receipts.receipt_sharing.data.repositoriesImpl.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositoriesImpl.CreatorsRecipesAmountRepository
import com.receipts.receipt_sharing.domain.apiServices.UnsafeImageLoader


const val NEW_RECIPES_NOTIFICATION_CHANNEL_ID = "NEW_RECIPES_CHANNEL_ID"

class RecipesApplication : Application(){

companion object{
    lateinit var appModule : IAppModule
}

    override fun onCreate() {
        super.onCreate()

        CreatorsRecipesAmountRepository.createInstance(this)
        AuthDataStoreRepository.createInstance(this)
        FileHelper.createInstance(this)
        UnsafeImageLoader.initialize(this)
        appModule = ManualAppModule(this)
    }

    private fun initializeMessageChannel(){
        val name = getString(R.string.new_recipes_notification_channel_name)
        val channel = NotificationChannel(
            NEW_RECIPES_NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
