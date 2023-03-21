package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.model.BluethoothDevice
import com.nada.tech.databinding.RowBluethoothDeviceBinding
import com.nada.tech.ui.adapter.BluethoothDeviceAdapter.Companion.ReaderViewHolder

class BluethoothDeviceAdapter(
    var mArrayList: ArrayList<BluethoothDevice>,
    var onClickItem: (device: BluethoothDevice) -> Unit
) : RecyclerView.Adapter<ReaderViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderViewHolder {
        val binding =
            RowBluethoothDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReaderViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: ReaderViewHolder, position: Int) {
        holder.bind(mArrayList[position])
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    companion object {
        class ReaderViewHolder(
            var binding: RowBluethoothDeviceBinding,
            var adapter: BluethoothDeviceAdapter
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(reader: BluethoothDevice) {
                binding.txtDeviceName.text = reader.deviceName
                binding.txtDeviceNo.text = reader.deviceNo
                binding.rootView.setOnClickListener {
                    adapter.onClickItem.invoke(reader)
                }
            }
        }
    }
}