package com.receipts.receipt_sharing.data.dataStore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserInfoSerializer : Serializer<UserInfo> {
    override val defaultValue: UserInfo
        get() = UserInfo()

    override suspend fun readFrom(input: InputStream): UserInfo {
        return try {
            Json.decodeFromString(
                deserializer = UserInfo.serializer(),
                string = input.readBytes().decodeToString()
            )
        }
        catch (e : SerializationException){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserInfo, output: OutputStream) {
        try {
            withContext(Dispatchers.IO) {
                output.write(
                    Json.encodeToString(
                        serializer = UserInfo.serializer(),
                        value = t
                    ).encodeToByteArray()
                )
            }
        }
        catch (e : SerializationException){
            e.printStackTrace()
        }
    }

}