package com.nada.tech.pagination

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.R

abstract class PagedAdapter<T : RecyclerView.ViewHolder, I>(
    context: Context,
    @LayoutRes val progressLayout: Int = R.layout.progress_circle_vertical,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] == null) VIEW_TYPE_PROGRESS
        else VIEW_TYPE_ITEM
    }

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PROGRESS) {
            ProgressViewHolder(inflater.inflate(progressLayout, parent, false))
        } else {
            getItemViewHolder(parent)
        }
    }

    abstract fun getItemViewHolder(parent: ViewGroup): T

    abstract val diffCallback: DiffUtil.ItemCallback<I>

    private val differ by lazy { AsyncListDiffer(this, diffCallback) }

    fun updateList(list: ArrayList<I?>?) {
        differ.submitList(list)
    }

    val currentList: MutableList<I> get() {
            return differ.currentList
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is ProgressViewHolder) {
            onItemBindViewHolder(holder, position)
        }
    }

    abstract fun onItemBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)


    class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)


    companion object {
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_PROGRESS = 2
    }
}