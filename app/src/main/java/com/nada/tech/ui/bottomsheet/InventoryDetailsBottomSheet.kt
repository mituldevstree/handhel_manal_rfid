package com.nada.tech.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nada.tech.R
import com.nada.tech.databinding.BottomSheetInventoryDetailsBinding
import com.nada.tech.model.InventoryModel

class InventoryDetailsBottomSheet : BaseHeaderBottomSheet() {

    private lateinit var binding: BottomSheetInventoryDetailsBinding
    private var inventoryModel: InventoryModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetInventoryDetailsBinding.inflate(inflater, container, false)
        headerView = binding.headerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitle(getString(R.string.inventory_detail))
        initUi()
    }

    private fun initUi() {
        binding.inventoryModel = inventoryModel
        binding.clickListener = this
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnLocate -> {
                if (inventoryModel == null) return
                navigation?.openFindInventoryActivity(inventoryModel?.tagData)
            }
        }
    }

    override fun show(activity: FragmentActivity): BaseBottomSheet? {
        return super.show(activity, InventoryDetailsBottomSheet::class.java.simpleName)
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
        fun newInstance(): InventoryDetailsBottomSheet {
            return InventoryDetailsBottomSheet()
        }

        fun newInstance(inventoryModel: InventoryModel): InventoryDetailsBottomSheet {
            val fragment = InventoryDetailsBottomSheet()
            fragment.inventoryModel = inventoryModel
            return fragment
        }
    }
}