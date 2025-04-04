package com.receipts.receipt_sharing.data.repositoriesImpl

import android.content.Context
import androidx.datastore.dataStore
import com.receipts.receipt_sharing.data.dataStore.UserInfoSerializer


data object PreferencesConsts{
    const val AUTH_PREFERENCE_NAME = "user-info.json"
    const val CREATORS_PREFERENCE_NAME = "creators-amount.json"

}

val Context.authDataStore by dataStore(fileName = PreferencesConsts.AUTH_PREFERENCE_NAME, serializer = UserInfoSerializer)

class AuthDataStoreRepository private constructor (
    context: Context
){
    val authDataStoreFlow = context.authDataStore.data
    private val dataStore = context.authDataStore

    suspend fun updateUserToken(newToken : String?){
        dataStore.updateData { t ->
            t.copy(token = newToken)
        }
    }
    suspend fun updateUserName(newUserName : String){
        dataStore.updateData {t ->
            t.copy(userName = newUserName)
        }
    }
    suspend fun updateSelectedPageIndex(newIndex : Int){
        dataStore.updateData {t ->
            t.copy(lastSelectedPageInd = newIndex)
        }
    }
    suspend fun updateImageUrl(newUrl : String){
        dataStore.updateData {t ->
            t.copy(imageUrl = newUrl)
        }
    }
    companion object{
        private var instance : AuthDataStoreRepository? = null

        fun createInstance(
            context : Context
        ){
            instance = AuthDataStoreRepository(context)
        }

        fun get() : AuthDataStoreRepository {
            return instance ?: throw IllegalStateException("Repository hasn`t been initialized")
        }
    }

}