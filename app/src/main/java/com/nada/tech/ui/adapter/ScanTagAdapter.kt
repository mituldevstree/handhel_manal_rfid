package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.databinding.RowScanTagBinding

class ScanTagAdapter(
    private var mArrayList: ArrayList<UHFInventoryItem>,
    private val withMaxLimit: Boolean = false,
) : RecyclerView.Adapter<ScanTagAdapter.Companion.DataViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            RowScanTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(mArrayList[position], position)
    }

    override fun getItemCount(): Int {
        return if (withMaxLimit) {
            if (mArrayList.size > 5) 5 else mArrayList.size
        } else mArrayList.size
    }

    companion object {
        class DataViewHolder(
            var binding: RowScanTagBinding,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: UHFInventoryItem, position: Int) {
                binding.txtEpc.text = String.format("${position + 1}. ${data.epc}")
            }
        }
    }
}