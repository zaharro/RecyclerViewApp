package com.example.weather


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.databinding.FragmentMainBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val myItems = mutableListOf<DisplayItem>()
    private lateinit var recyclerActivityAdapter: RecyclerActivityAdapter

    private val drawableImageIds = listOf(
        R.drawable.image_1,
        R.drawable.image_2,
        R.drawable.image_3
    )
    private var imageIndex = 0

    private var deletedItem: DisplayItem? = null
    private var deletedItemPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialData()
        setupRecyclerView()
        setupFab()
    }

    private fun setupInitialData() {
        myItems.apply {
            add(DisplayItem.Header("Мои путевые заметки"))
            add(
                DisplayItem.TextItem(
                    "Инструкция",
                    "Зажмите элемент для перемещения. Смахните вправо для удаления."
                )
            )

            add(DisplayItem.ImageItem(drawableImageIds[0], "Фото #1"))
            add(DisplayItem.ImageItem(drawableImageIds[1], "Фото #2"))
            add(DisplayItem.ImageItem(drawableImageIds[2], "Фото #3"))
        }
    }

    private fun setupRecyclerView() {
        recyclerActivityAdapter = RecyclerActivityAdapter(myItems)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerActivityAdapter
        }

        val itemTouchHelper = ItemTouchHelper(createItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            // Поочередное добавление текстовых и графических элементов
            if (myItems.size % 2 == 0) {
                val newImageItem = DisplayItem.ImageItem(
                    drawableImageIds[imageIndex],
                    "Добавлено изображение #${imageIndex + 1}"
                )
                recyclerActivityAdapter.addItem(newImageItem)
                imageIndex =
                    (imageIndex + 1) % drawableImageIds.size
                Toast.makeText(context, "Добавлено изображение", Toast.LENGTH_SHORT).show()

            } else {
                val newItem = DisplayItem.TextItem(
                    "Описание к фото #${myItems.size}",
                    "Текст с описанием."
                )
                recyclerActivityAdapter.addItem(newItem)
                Toast.makeText(context, "Добавлен текст ", Toast.LENGTH_SHORT).show()
            }

            binding.recyclerView.smoothScrollToPosition(recyclerActivityAdapter.itemCount - 1)
        }
    }

    private fun createItemTouchHelperCallback(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Исключение заголовка из перетаскивания и свайпа
                if (recyclerActivityAdapter.getItemViewType(viewHolder.getBindingAdapterPosition()) == RecyclerActivityAdapter.VIEW_TYPE_HEADER) {
                    return 0
                }

                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.getBindingAdapterPosition()
                val toPosition = target.getBindingAdapterPosition()

                // Запрет перетаскивания на позицию заголовка
                if (recyclerActivityAdapter.getItemViewType(toPosition) == RecyclerActivityAdapter.VIEW_TYPE_HEADER) {
                    return false
                }

                recyclerActivityAdapter.onItemMove(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.getBindingAdapterPosition()
                deletedItem =
                    recyclerActivityAdapter.removeItem(position) // Удаление и сохранение элемента для отмены удаления
                deletedItemPosition = position

                Snackbar.make(binding.root, "Элемент удален", Snackbar.LENGTH_LONG)
                    .setAction("Отменить") {
                        deletedItem?.let { item ->
                            recyclerActivityAdapter.restoreItem(
                                item,
                                deletedItemPosition
                            )
                        }
                    }
                    .show()
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}