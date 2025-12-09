package com.example.weather


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ItemHeaderBinding
import com.example.weather.databinding.ItemImageBinding
import com.example.weather.databinding.ItemTextBinding


class MVVMRecyclerActivityAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<DisplayItem> = emptyList()

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_TEXT_ITEM = 1
        const val VIEW_TYPE_IMAGE_ITEM = 2
    }

    // Метод для обновления данных в адаптере
    fun submitList(newList: List<DisplayItem>) {
        items = newList // Замена старого списка новым
        notifyDataSetChanged() // Уведомление адаптера о полном изменении данных
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DisplayItem.Header -> VIEW_TYPE_HEADER
            is DisplayItem.TextItem -> VIEW_TYPE_TEXT_ITEM
            is DisplayItem.ImageItem -> VIEW_TYPE_IMAGE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }

            VIEW_TYPE_TEXT_ITEM -> {
                val binding = ItemTextBinding.inflate(inflater, parent, false)
                TextViewHolder(binding)
            }

            VIEW_TYPE_IMAGE_ITEM -> {
                val binding = ItemImageBinding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Неизвестный тип: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DisplayItem.Header -> (holder as HeaderViewHolder).bind(item)
            is DisplayItem.TextItem -> (holder as TextViewHolder).bind(item)
            is DisplayItem.ImageItem -> (holder as ImageViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    // Методы addItem, removeItem, restoreItem, onItemMove удалены, т.к. они теперь управляются из ViewModel,
    // а adapter только отображает новый список, полученный через submitList.

    /*    fun addItem(item: DisplayItem) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }

        fun onItemMove(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }


        fun removeItem(position: Int): DisplayItem {
            val deletedItem = items.removeAt(position)
            notifyItemRemoved(position)
            return deletedItem
        }


        fun restoreItem(item: DisplayItem, position: Int) {
            items.add(position, item)
            notifyItemInserted(position)
        }*/

    class HeaderViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: DisplayItem.Header) {
            binding.tvHeaderTitle.text = header.title
        }
    }

    class TextViewHolder(private val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(textItem: DisplayItem.TextItem) {
            binding.tvItemTextTitle.text = textItem.text
            binding.tvItemTextDescription.text = textItem.description
        }
    }

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: DisplayItem.ImageItem) {
            binding.tvItemImageCaption.text = imageItem.caption
            binding.ivItemImage.setImageResource(imageItem.resourceId)
        }
    }
}
