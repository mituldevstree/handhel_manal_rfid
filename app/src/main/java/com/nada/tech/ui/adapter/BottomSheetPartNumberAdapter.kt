package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.RowTextviewBinding
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import javax.inject.Inject

class BottomSheetPartNumberAdapter @Inject constructor() :
    RecyclerView.Adapter<BottomSheetPartNumberAdapter.Companion.DataViewHolder?>() {

    var mArrayList = ArrayList<PartModel>()
    var onItemClickCallback: ((data: PartModel) -> Unit)? = null

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
            var adapter: BottomSheetPartNumberAdapter,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: PartModel) {
                binding.txtEpc.text = data.partNumber
                binding.linRoot.setOnClickListener { adapter.onItemClickCallback?.invoke(data) }
            }
        }
    }
}