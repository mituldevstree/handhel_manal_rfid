package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.databinding.RowRfidTagBinding
import com.nada.tech.model.PartModel
import com.rscja.deviceapi.entity.UHFTAGInfo
import okhttp3.internal.cacheGet

class ScanRFIDAdapter(
    private var mArrayList: ArrayList<UHFInventoryItem>,
) :
    RecyclerView.Adapter<ScanRFIDAdapter.Companion.DataViewHolder?>() {
    var onItemClickCallback: ((UHFInventoryItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            RowRfidTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(mArrayList[position])
        holder.binding.rootView.setOnClickListener {
            onItemClickCallback?.invoke(
                mArrayList[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun setData(mArrayList: ArrayList<UHFInventoryItem>) {
        this.mArrayList = mArrayList
        notifyDataSetChanged()
    }

    fun add(uhfInventoryItem: UHFInventoryItem) {
        this.mArrayList.add(uhfInventoryItem)
        notifyDataSetChanged()
    }

    companion object {
        class DataViewHolder(
            var binding: RowRfidTagBinding,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: UHFInventoryItem) {
                with(binding) {
                    txtEpc.text = String.format("EPC : ${data.epc}")
                    txtCount.text = String.format("Count : ${data.count}")
                    txtRSSI.text = String.format("RSSI : ${data.rssi}")
                }
            }
        }
    }
}