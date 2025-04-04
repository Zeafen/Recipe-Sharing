package com.receipts.receipt_sharing.presentation.creators.creatorPage

sealed interface CreatorPageEvent {
    data class LoadCreator(val creatorID: String) : CreatorPageEvent
    data object ChangeFollows : CreatorPageEvent
    data object ReloadRecipes : CreatorPageEvent
    data class SetExpandAboutMe(val expand : Boolean) : CreatorPageEvent
}