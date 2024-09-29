package com.receipts.receipt_sharing.domain

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.receipts.NEW_RECIPES_NOTIFICATION_CHANNEL_ID
import com.receipts.receipt_sharing.R
import com.receipts.receipt_sharing.data.repositories.AuthDataStoreRepository
import com.receipts.receipt_sharing.data.repositories.CreatorsRecipesAmountRepository
import com.receipts.receipt_sharing.data.repositories.CreatorsRepository
import com.receipts.receipt_sharing.data.repositories.RecipesRepositoryImpl
import com.receipts.receipt_sharing.domain.helpers.isPermissionGranted
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AnnouncementWorker @Inject constructor (
    private val recipesRepo : RecipesRepositoryImpl,
    private val creatorsRepo : CreatorsRepository,
    private val appContext : Context,
    params : WorkerParameters
) : CoroutineWorker(
    appContext,
    params
) {


    private val authDataStore = AuthDataStoreRepository.get()
    private val creatorsAmount = CreatorsRecipesAmountRepository.get()

    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val token = authDataStore.authDataStoreFlow.first().token
        if(token != null){
            val follows = creatorsRepo.getFollows(token)
            if(follows.data == null)
                return Result.failure()
            follows.data.indices.forEach {
                val recipes = recipesRepo.getRecipesByCreator(token, follows.data[it].userID).data?: emptyList()
                val creatorCount = creatorsAmount.creatorsRecipesDataStoreFlow.first()[follows.data[it].userID]
                if(creatorCount != null && recipes.count() - creatorCount > 0){
                    postNotification(follows.data[it].nickname, it,recipes.count() - creatorCount)
                }
            }
        }
        return Result.failure()
    }


    private fun postNotification(creatorName : String, notificationID : Int, amountRecipes : Int){
        val notification = Notification.Builder(appContext, NEW_RECIPES_NOTIFICATION_CHANNEL_ID)
            .setContentText(appContext.getString(R.string.new_recipes_message_title))
            .setContentTitle(appContext.getString(R.string.new_recipes_message_text, creatorName, amountRecipes))
            .setAutoCancel(true)
            .build()
        if(isPermissionGranted(appContext, android.Manifest.permission.POST_NOTIFICATIONS)){
            notificationManager.notify(notificationID, notification)
        }
    }

    companion object{
        const val WORKER_ID = 1
        const val WORK_NAME = "new recipes announcement"
    }
}