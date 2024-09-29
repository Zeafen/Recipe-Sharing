package com.receipts

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.repositories.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositories.CreatorsRecipesAmountRepository
import com.receipts.receipt_sharing.domain.helpers.FileHelper
import dagger.hilt.android.HiltAndroidApp


const val NEW_RECIPES_NOTIFICATION_CHANNEL_ID = "NEW_RECIPES_CHANNEL_ID"

@HiltAndroidApp
class RecipesApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        CreatorsRecipesAmountRepository.createInstance(this)
        AuthDataStoreRepository.createInstance(this)
        FileHelper.createInstance(this)

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
