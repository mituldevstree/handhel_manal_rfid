package com.nada.tech.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.ItemInventoryBinding
import com.nada.tech.model.InventoryModel
import com.nada.tech.ui.adapter.InventoryAdapter.Companion.DataViewHolder
import com.nada.tech.pagination.PagedAdapter
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class InventoryAdapter @Inject constructor(
    @ActivityContext context: Context,
) : PagedAdapter<DataViewHolder, InventoryModel>(context) {
    var onItemClickCallback: ((InventoryModel) -> Unit)? = null

    override fun getItemViewHolder(parent: ViewGroup): DataViewHolder {
        return DataViewHolder(
            ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            this
        )
    }

    override fun onItemBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) holder.bind(currentList[position])
    }

    override val diffCallback: DiffUtil.ItemCallback<InventoryModel>
        get() = object : DiffUtil.ItemCallback<InventoryModel>() {
            override fun areItemsTheSame(
                oldItem: InventoryModel,
                newItem: InventoryModel,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: InventoryModel,
                newItem: InventoryModel,
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

    companion object {
        class DataViewHolder(
            var binding: ItemInventoryBinding,
            var adapter: InventoryAdapter,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(inventoryModel: InventoryModel) {
                binding.inventoryModel = inventoryModel
                binding.rootView.setOnClickListener { adapter.onItemClickCallback?.invoke(inventoryModel) }
            }
        }
    }

}