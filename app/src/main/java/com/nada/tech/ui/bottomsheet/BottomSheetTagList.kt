package com.nada.tech.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.nada.tech.R
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.databinding.BottomSheetRecyclerviewBinding
import com.nada.tech.ui.adapter.ScanTagAdapter

class BottomSheetTagList : BaseHeaderBottomSheet() {

    lateinit var binding: BottomSheetRecyclerviewBinding
    private var adapter: ScanTagAdapter? = null
    private var mArrayList = ArrayList<UHFInventoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetRecyclerviewBinding.inflate(inflater, container, false)
        headerView = binding.headerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setExpanded(base?.isTablet() == true)
        changeTitle(getString(R.string.scanned_items))
        initUi()
    }

    private fun initUi() {
        adapter = ScanTagAdapter(mArrayList)
        val spanCount = if (base?.isTablet() == true) 2 else 1
        binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), spanCount)
        binding.recyclerView.adapter = adapter
    }

    override fun show(activity: FragmentActivity): BaseBottomSheet? {
        return super.show(activity, BottomSheetTagList::class.java.simpleName)
    }

    companion object {
        fun newInstance(mArrayList: ArrayList<UHFInventoryItem>): BottomSheetTagList {
            val fragment = BottomSheetTagList()
            fragment.mArrayList = mArrayList
            return fragment
        }
    }
}