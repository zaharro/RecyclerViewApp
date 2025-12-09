package com.example.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Collections

class MainViewModel : ViewModel() {

    private val _items = MutableLiveData<MutableList<DisplayItem>>()

    val items: LiveData<MutableList<DisplayItem>> = _items

    private val drawableImageIds = listOf(
        R.drawable.image_1,
        R.drawable.image_2,
        R.drawable.image_3
    )
    private var imageIndex = 0


    init {
        val initialList = mutableListOf<DisplayItem>().apply {
            add(DisplayItem.Header("Мои путевые заметки"))
            add(
                DisplayItem.TextItem(
                    "Инструкции",
                    "Зажмите элемент для перемещения. Смахните вправо для удаления."
                )
            )
            add(DisplayItem.ImageItem(drawableImageIds[0], "Фото #1"))
            add(DisplayItem.ImageItem(drawableImageIds[1], "Фото #2"))
            add(DisplayItem.ImageItem(drawableImageIds[2], "Фото #3"))
        }
        _items.value = initialList // Начальное значение LiveData
    }

    fun addItem() {
        // Получение текущего списка, создание его копии для изменения
        val currentList = _items.value?.toMutableList() ?: mutableListOf()

        if (currentList.size % 2 == 0) {
            val newImageItem = DisplayItem.ImageItem(
                drawableImageIds[imageIndex],
                "Добавлено изображение #${imageIndex + 1}"
            )
            currentList.add(newImageItem)
            imageIndex = (imageIndex + 1) % drawableImageIds.size
        } else {
            val newItem = DisplayItem.TextItem(
                "Описание к фото #${currentList.size}",
                "Текст с описанием"
            )
            currentList.add(newItem)
        }
        _items.value = currentList // Обновление LiveData
    }

    fun removeItem(position: Int): DisplayItem? {
        val currentList = _items.value?.toMutableList() ?: return null
        if (position < 0 || position >= currentList.size) return null

        val removedItem = currentList.removeAt(position)
        _items.value = currentList
        return removedItem
    }

    fun restoreItem(item: DisplayItem, position: Int) {
        val currentList = _items.value?.toMutableList() ?: mutableListOf()
        if (position >= 0 && position <= currentList.size) {
            currentList.add(position, item)
            _items.value = currentList
        }
    }


    fun moveItem(fromPosition: Int, toPosition: Int) {
        val currentList = _items.value?.toMutableList() ?: return
        if (fromPosition < 0 || fromPosition >= currentList.size ||
            toPosition < 0 || toPosition >= currentList.size
        ) return

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(currentList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(currentList, i, i - 1)
            }
        }
        _items.value = currentList
    }

    // Исключение заголовка из перетаскивания и свайпа
    fun isHeader(position: Int): Boolean {
        val currentList = _items.value ?: return false
        if (position < 0 || position >= currentList.size) return false
        return currentList[position] is DisplayItem.Header
    }
}