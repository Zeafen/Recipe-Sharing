package com.receipts.receipt_sharing.data.helpers

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun isPermissionGranted(
    context : Context,
    permission : String
) : Boolean{
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun isPermissionsGranted(
    context : Context,
    permissions  : List<String>
) : Boolean{
    return permissions.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}