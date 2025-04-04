package com.receipts.receipt_sharing.data.helpers

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class FileHelper private constructor(private val applicationContext: Context) {
    /**
     *Obtains file path from uri
     * @param uri file uri
     * @return file path
     */
    fun getFileFromUri(uri: Uri?): String? {
        return uri?.let {
            val cursor = applicationContext.contentResolver.query(
                it, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null
            )
            val path = cursor?.let { curs ->
                curs.moveToFirst()

                val columnIndex = curs.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (columnIndex != -1)
                    cursor.getString(columnIndex)
                else ""
            } ?: ""
            cursor?.close()
            path
        }
    }

    companion object {
        private var _instance: FileHelper? = null

        fun get(): FileHelper {
            return _instance ?: throw NotImplementedError();
        }

        fun createInstance(applicationContext: Context) {
            _instance = FileHelper(applicationContext)
        }
    }
}