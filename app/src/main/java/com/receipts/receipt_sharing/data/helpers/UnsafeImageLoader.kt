package com.receipts.receipt_sharing.data.helpers

import android.content.Context
import coil.ImageLoader
import coil.imageLoader
import coil.util.DebugLogger

class UnsafeImageLoader {
    companion object{
        private var _imageLoader : ImageLoader? = null

        fun initialize(context: Context){
            _imageLoader = context.imageLoader.newBuilder()
                .logger(DebugLogger())
                .okHttpClient(UnsafeOkHttpClient.getOkHttpClient())
                .build()
        }

        fun getInstance() : ImageLoader = _imageLoader ?: throw NotImplementedError()
    }
}