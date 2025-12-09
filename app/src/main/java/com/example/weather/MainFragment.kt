package com.example.weather


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.databinding.FragmentMainBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerActivityAdapter: MVVMRecyclerActivityAdapter
    private var lastDeletedItem: DisplayItem? = null
    private var lastDeletedItemPosition: Int = -1

    /*     private val myItems = mutableListOf<DisplayItem>()

 private val drawableImageIds = listOf(
       R.drawable.image_1,
       R.drawable.image_2,
       R.drawable.image_3
   )
   private var imageIndex = 0

   private var deletedItem: DisplayItem? = null
   private var deletedItemPosition: Int = -1


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
   }*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeViewModel() // Наблюдение за данными из ViewModel
    }

    private fun setupRecyclerView() {
        recyclerActivityAdapter = MVVMRecyclerActivityAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerActivityAdapter
        }

        val itemTouchHelper = ItemTouchHelper(createItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            viewModel.addItem()
            Toast.makeText(context, "Элемент добавлен", Toast.LENGTH_SHORT).show()
            binding.recyclerView.postDelayed({
                binding.recyclerView.smoothScrollToPosition(recyclerActivityAdapter.itemCount - 1)
            }, 100)
        }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            recyclerActivityAdapter.submitList(items)
        }
    }

    private fun createItemTouchHelperCallback(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.getBindingAdapterPosition()
                // Исключение заголовка из перетаскивания и свайпа
                if (position != RecyclerView.NO_POSITION && viewModel.isHeader(position)) {
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

                // Исключение перетаскивания на позицию заголовка
                if (toPosition != RecyclerView.NO_POSITION && viewModel.isHeader(toPosition)) {
                    return false
                }

                viewModel.moveItem(
                    fromPosition,
                    toPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.getBindingAdapterPosition()
                if (position == RecyclerView.NO_POSITION) return

                val removedItem = viewModel.removeItem(position)
                removedItem?.let {
                    lastDeletedItem = it
                    lastDeletedItemPosition = position

                    Snackbar.make(binding.root, "Элемент удален", Snackbar.LENGTH_LONG)
                        .setAction("Отменить") {
                            // Восстанавление элемента через ViewModel
                            lastDeletedItem?.let { itemToRestore ->
                                viewModel.restoreItem(itemToRestore, lastDeletedItemPosition)
                            }
                        }
                        .show()
                }
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