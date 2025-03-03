package com.receipts.receipt_sharing.presentation.creators

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.receipts.receipt_sharing.R

enum class ProfileConfigScreens(
    @DrawableRes val iconRes : Int,
    @StringRes val nameRes : Int
){
    MainScreen(R.drawable.user_info_page_ic,R.string.profile_page_header),
    EditInfo(R.drawable.info_ic, R.string.edit_info_lbl),
    Security(R.drawable.security_ic, R.string.security_lbl),
    ChangePassword(R.drawable.passw_ic, R.string.change_password_lbl),
    Settings(R.drawable.settings_ic, R.string.settings_lbl),
}