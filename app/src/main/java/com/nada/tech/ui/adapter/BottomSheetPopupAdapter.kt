package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.RowTextviewBinding
import com.nada.tech.model.PopupModel
import javax.inject.Inject

class BottomSheetPopupAdapter @Inject constructor() :
    RecyclerView.Adapter<BottomSheetPopupAdapter.Companion.DataViewHolder?>() {

    var mArrayList = ArrayList<PopupModel>()
    var onItemClickCallback: ((data: PopupModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            RowTextviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(mArrayList[position])
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    companion object {
        class DataViewHolder(
            var binding: RowTextviewBinding,
            var adapter: BottomSheetPopupAdapter,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: PopupModel) {
                binding.txtEpc.text = data.value
                binding.linRoot.setOnClickListener { adapter.onItemClickCallback?.invoke(data) }
            }
        }
    }
}