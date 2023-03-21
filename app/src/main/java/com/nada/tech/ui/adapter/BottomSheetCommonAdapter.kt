package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.RowTextviewBinding

class BottomSheetCommonAdapter(
    private var mArrayList: ArrayList<String>,
    private val onItemClickCallback: (data: String) -> Unit,
) :
    RecyclerView.Adapter<BottomSheetCommonAdapter.Companion.DataViewHolder?>() {

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
            var adapter: BottomSheetCommonAdapter,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: String) {
                binding.txtEpc.text = data
                binding.linRoot.setOnClickListener { adapter.onItemClickCallback.invoke(data) }
            }
        }
    }
}