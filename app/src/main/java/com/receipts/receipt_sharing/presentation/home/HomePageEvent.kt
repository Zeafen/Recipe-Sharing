package com.receipts.receipt_sharing.presentation.home

sealed interface HomePageEvent {
    data object LoadData : HomePageEvent
    data object LoadPublishers : HomePageEvent
    data object LoadPopulars : HomePageEvent
    data object LoadRecents : HomePageEvent
}