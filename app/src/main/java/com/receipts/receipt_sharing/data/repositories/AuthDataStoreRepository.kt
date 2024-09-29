package com.receipts.receipt_sharing.data.repositories

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.receipts.receipt_sharing.data.dataStore.CreatorsMapSerializer
import com.receipts.receipt_sharing.data.dataStore.UserInfoSerializer


data object PreferencesConsts{
    val TOKEN_KEY = stringPreferencesKey("jwt-token")
    const val AUTH_PREFERENCE_NAME = "user-info.json"
    const val CREATORS_PREFERENCE_NAME = "creators-amount.json"

}

val Context.authDataStore by dataStore(fileName = PreferencesConsts.AUTH_PREFERENCE_NAME, serializer = UserInfoSerializer)
val Context.creatorsRecipesDataStore by dataStore(fileName = PreferencesConsts.CREATORS_PREFERENCE_NAME, serializer = CreatorsMapSerializer)

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

        fun get() : AuthDataStoreRepository{
            return instance ?: throw IllegalStateException("Repository hasn`t been initialized")
        }
    }

}

class CreatorsRecipesAmountRepository private constructor(
    context: Context
){
    val creatorsRecipesDataStoreFlow = context.creatorsRecipesDataStore.data
    private val dataStore = context.creatorsRecipesDataStore

    suspend fun setRecipesAmount(creatorID : String, amount : Int){
        dataStore.updateData {
            it.apply {
                this.toMutableMap()[creatorID] = amount
            }
        }
    }

    companion object{
        private var instance : CreatorsRecipesAmountRepository? = null

        fun createInstance(
            context : Context
        ){
            instance = CreatorsRecipesAmountRepository(context)
        }

        fun get() : CreatorsRecipesAmountRepository{
            return instance ?: throw IllegalStateException("Repository hasn`t been initialized")
        }
    }
}