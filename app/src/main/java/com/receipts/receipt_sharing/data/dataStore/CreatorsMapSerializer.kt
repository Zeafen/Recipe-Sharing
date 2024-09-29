package com.receipts.receipt_sharing.data.dataStore

import androidx.datastore.core.Serializer
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object CreatorsMapSerializer : Serializer<Map<String, Int>> {
    override val defaultValue: HashMap<String, Int>
        get() = hashMapOf()

    override suspend fun readFrom(input: InputStream): Map<String, Int> {
        return try {
            val typeRef = object : TypeReference<HashMap<String, Int>>(){}
            val mapper = ObjectMapper()
            mapper.readValue(input.readBytes(), typeRef)
        }
        catch (ex : Exception){
            defaultValue
        }
    }

    override suspend fun writeTo(value: Map<String, Int>, output: OutputStream) {
        try {
            val mapper = ObjectMapper()
            withContext(Dispatchers.IO){
                output.write(
                    mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(value)
                        .encodeToByteArray()
                )
            }
        }
        catch (ex : Exception){
            ex.printStackTrace()
        }
    }
}