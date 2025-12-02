package com.example.weather

sealed class DisplayItem {
    data class Header(val title: String) : DisplayItem()
    data class TextItem(val text: String, val description: String) : DisplayItem()
    data class ImageItem(val resourceId: Int, val caption: String) : DisplayItem()
}