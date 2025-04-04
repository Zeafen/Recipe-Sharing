package com.receipts.receipt_sharing.data.helpers

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * checks if permission is granted
 * @param context context for which to check permissions
 * @param permission permission to check
 * @return true if permission is granted; otherwise - false
 */
fun isPermissionGranted(
    context : Context,
    permission : String
) : Boolean{
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * checks if permissions are granted
 * @param context context for which to check permissions
 * @param permissions permissions to check
 * @return true if permissions are granted; otherwise - false
 */
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