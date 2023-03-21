package com.nada.tech.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.RowRfidTagBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.pagination.PagedAdapter
import com.nada.tech.utility.gone
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LocationAdapter @Inject constructor(
    @ActivityContext context: Context,
) : PagedAdapter<LocationAdapter.Companion.DataViewHolder, PopupModel>(context) {
    var onItemClickCallback: ((PopupModel) -> Unit)? = null

    override fun getItemViewHolder(parent: ViewGroup): DataViewHolder {
        return DataViewHolder(
            RowRfidTagBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            this
        )
    }

    override fun onItemBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) holder.bind(currentList[position])
    }

    override val diffCallback: DiffUtil.ItemCallback<PopupModel>
        get() = object : DiffUtil.ItemCallback<PopupModel>() {
            override fun areItemsTheSame(
                oldItem: PopupModel,
                newItem: PopupModel,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PopupModel,
                newItem: PopupModel,
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

    companion object {
        class DataViewHolder(
            var binding: RowRfidTagBinding,
            var adapter: LocationAdapter,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(model: PopupModel) {
                binding.txtCount.text = "Location Code:--" + model.LocationCode
                binding.txtEpc.text = model.value
                binding.txtRSSI.gone()
                binding.root.setOnClickListener { adapter.onItemClickCallback?.invoke(model) }
            }
        }
    }

}