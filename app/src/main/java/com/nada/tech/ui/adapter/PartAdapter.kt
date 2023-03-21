package com.nada.tech.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.ItemInventoryBinding
import com.nada.tech.databinding.ItemPartBinding
import com.nada.tech.model.PartModel
import com.nada.tech.pagination.PagedAdapter
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PartAdapter @Inject constructor(
    @ActivityContext context: Context,
) : PagedAdapter<PartAdapter.Companion.DataViewHolder, PartModel>(context) {
    var onItemClickCallback: ((PartModel) -> Unit)? = null

    override fun getItemViewHolder(parent: ViewGroup): DataViewHolder {
        return DataViewHolder(
            ItemPartBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            this
        )
    }

    override fun onItemBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) holder.bind(currentList[position])
    }

    override val diffCallback: DiffUtil.ItemCallback<PartModel>
        get() = object : DiffUtil.ItemCallback<PartModel>() {
            override fun areItemsTheSame(
                oldItem: PartModel,
                newItem: PartModel,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PartModel,
                newItem: PartModel,
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

    companion object {
        class DataViewHolder(
            var binding: ItemPartBinding,
            var adapter: PartAdapter,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(PartModel: PartModel) {
                binding.partModel = PartModel
                binding.rootView.setOnClickListener {
                    adapter.onItemClickCallback?.invoke(
                        PartModel
                    )
                }
            }
        }
    }

}