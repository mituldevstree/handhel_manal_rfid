package com.nada.tech.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nada.tech.R
import com.nada.tech.databinding.BottomSheetRecyclerviewBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.ui.adapter.BottomSheetPopupAdapter
import com.nada.tech.utility.decorator.DividerItemDecorator
import com.nada.tech.utility.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetPopupMenu : BaseHeaderBottomSheet() {

    private lateinit var binding: BottomSheetRecyclerviewBinding
    private var clickListener: BottomSheetItemClickListener? = null

    @Inject
    lateinit var adapter: BottomSheetPopupAdapter
    private var mArrayList = ArrayList<PopupModel>()
    private var title = ""

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
        changeTitle(title)
        initUi()
    }

    private fun initUi() {
        if (showAddBtn) {
            binding.btnAdd.visible()
        }
        binding.recyclerView.addItemDecoration(
            DividerItemDecorator(getDrawable(requireContext(), R.drawable.divider_decorator_gray))
        )
        adapter.mArrayList = mArrayList
        adapter.onItemClickCallback = {
            clickListener?.onItemClick(it)
            dismiss()
        }
        binding.recyclerView.adapter = adapter
    }

    override fun show(activity: FragmentActivity): BaseBottomSheet? {
        return super.show(activity, BottomSheetPopupMenu::class.java.simpleName)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        if (base?.isTablet() != true) return dialog

        dialog.setOnShowListener {
            val bottomDialog = it as BottomSheetDialog
            val bottomSheet =
                (bottomDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?)
                    ?: return@setOnShowListener
            val displayMetrics = requireActivity().resources.displayMetrics
            val height = displayMetrics.heightPixels
            val maxHeight = (height * 0.90).toInt()
            BottomSheetBehavior.from(bottomSheet).peekHeight = maxHeight
        }
        return dialog
    }

    companion object {
        fun newInstance(
            title: String,
            showAddBtn: Boolean,
            mArrayList: ArrayList<PopupModel>,
            itemClickListenerListener: BottomSheetItemClickListener,
        ): BottomSheetPopupMenu {
            val fragment = BottomSheetPopupMenu()
            fragment.title = title
            fragment.mArrayList = mArrayList
            fragment.showAddBtn = showAddBtn
            fragment.clickListener = itemClickListenerListener
            return fragment
        }
    }

    interface BottomSheetItemClickListener {
        fun onItemClick(data: PopupModel)
    }
}