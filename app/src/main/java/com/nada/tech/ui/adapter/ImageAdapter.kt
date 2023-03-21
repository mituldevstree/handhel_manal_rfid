package com.nada.tech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.filepickersample.model.Media
import com.nada.tech.databinding.ItemAddImageBinding
import com.nada.tech.utility.loadImage
import javax.inject.Inject

class ImageAdapter @Inject constructor() : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var arrayList = ArrayList<Media>()
    var clickCallback: ((media: Media, position: Int) -> Unit)? = null
    var deleteCallback: ((media: Media, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAddImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, this)
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }

    class ViewHolder(val binding: ItemAddImageBinding, private val adapter: ImageAdapter) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(media: Media) {
            binding.imageView.loadImage(media.url)

            binding.imageView.setOnClickListener {
                adapter.clickCallback?.invoke(media,
                    adapterPosition)
            }
            binding.ivDelete.setOnClickListener {
                adapter.deleteCallback?.invoke(media, adapterPosition)
            }
        }
    }

}