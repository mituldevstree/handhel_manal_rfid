package com.nada.tech.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nada.tech.R
import com.nada.tech.databinding.BottomSheetInventoryFilterBinding
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.utility.DateTimeUtil.DDMMYYYY
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class InventoryFilterBottomSheet(val filterApplyCallback: () -> Unit) : BaseHeaderBottomSheet() {

    private lateinit var binding: BottomSheetInventoryFilterBinding
    private val viewModel by viewModels<AssetViewModel>()

    private var selectedDate: Calendar? = null
    private var mAssetType = ArrayList<PopupModel>()
    private var mPartList = ArrayList<PartModel>()
    private var mLocations = ArrayList<PopupModel>()
    private var mAssetCategory = ArrayList<PopupModel>()
    var filterMap = HashMap<String, Any>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetInventoryFilterBinding.inflate(inflater, container, false)
        headerView = binding.headerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitle(getString(R.string.filter))
        initUi()
    }

    private fun initUi() {
        binding.clickListener = this
        resetFilter()
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.edtAssetType -> {
                if (mAssetType.isNullOrEmpty()) {
                    base?.toast(getString(R.string.no_asset_type_found))
                    return
                }
                BottomSheetPopupMenu.newInstance(
                    getString(R.string.asset_type), showAddBtn = false,
                    mAssetType,
                    object : BottomSheetPopupMenu.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtAssetType.setText(data.value)
                            binding.edtAssetType.tag = data.id
                            resetCategory()
                            getAssetCategories(data.id)
                        }
                    }).show(requireActivity())
            }
            binding.edtAssetCategory -> {
                if (mAssetCategory.isNullOrEmpty()) {
                    base?.toast(getString(R.string.no_asset_categories_found))
                    return
                }
                BottomSheetPopupMenu.newInstance(
                    getString(R.string.asset_category),false,
                    mAssetCategory,
                    object : BottomSheetPopupMenu.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtAssetCategory.setText(data.value)
                            binding.edtAssetCategory.tag = data.id
                        }
                    }).show(requireActivity())
            }
            binding.edtPart -> {
                if (mPartList.isNullOrEmpty()) {
                    base?.toast(getString(R.string.no_part_found))
                    return
                }
                BottomSheetPartNumber.newInstance(
                    getString(R.string.part),
                    mPartList,
                    object : BottomSheetPartNumber.BottomSheetItemClickListener {
                        override fun onItemClick(data: PartModel) {
                            binding.edtPart.setText(data.partNumber)
                            binding.edtPart.tag = data.partId
                        }
                    }).show(requireActivity())
            }
            binding.edtLocation -> {
                if (mLocations.isNullOrEmpty()) {
                    base?.toast(getString(R.string.no_locations_found))
                    return
                }
                BottomSheetPopupMenu.newInstance(
                    getString(R.string.location),
                    false,
                    mLocations,
                    object : BottomSheetPopupMenu.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtLocation.setText(data.value)
                            binding.edtLocation.tag = data.id
                        }
                    }).show(requireActivity())
            }
            binding.edtCheckedOutTo -> {
                if (mLocations.isNullOrEmpty()) {
                    base?.toast(getString(R.string.no_locations_found))
                    return
                }
                BottomSheetPopupMenu.newInstance(
                    getString(R.string.checked_out_to),
                    false,
                    mLocations,
                    object : BottomSheetPopupMenu.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtCheckedOutTo.setText(data.value)
                            binding.edtCheckedOutTo.tag = data.id
                        }
                    }).show(requireActivity())
            }
            binding.edtIdleAsset -> {
                if (selectedDate == null) selectedDate = Calendar.getInstance()
                base?.openDatePickerDialog(
                    "",
                    selectedDate!!,
                    { _, year, month, dayOfMonth ->
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.set(year, month, dayOfMonth)
                        selectedDate = calendar
                        binding.edtIdleAsset.setText(DDMMYYYY.format(calendar.time))
                    },
                    isFuture = false,
                    isPast = false
                )
            }
            binding.btnApply -> {
                updateFilterMap()
                filterApplyCallback.invoke()
                dismiss()
            }

            binding.btnReset -> {
                resetFilter()
            }
        }
    }

    private fun updateFilterMap() {
        filterMap.clear()

        filterMap["AssetTypeId"] = binding.edtAssetType.tag
        filterMap["AssetCategoryId"] = binding.edtAssetCategory.tag
        filterMap["PartId"] = binding.edtPart.tag
        filterMap["LocationId"] = binding.edtLocation.tag
//        filterMap["checked_out_to"] = binding.edtCheckedOutTo.tag
        if (selectedDate != null) filterMap["IdleAssetDate"] = DDMMYYYY.format(selectedDate!!.time)
        filterMap["CheckIn"] = binding.chkInStock.isChecked
        filterMap["CheckOut"] = binding.chkCheckedOut.isChecked
    }

    private fun resetFilter() {
        binding.edtAssetType.setText(getString(R.string.all))
        binding.edtAssetType.tag = ""
        binding.edtPart.setText(getString(R.string.all))
        binding.edtPart.tag = ""
        binding.edtLocation.setText(getString(R.string.all))
        binding.edtLocation.tag = ""
        binding.edtCheckedOutTo.setText(getString(R.string.all))
        binding.edtCheckedOutTo.tag = ""
        binding.chkInStock.isChecked = true
        binding.chkCheckedOut.isChecked = true
        selectedDate = null
        binding.edtIdleAsset.clear()
        resetCategory()
    }

    override fun show(activity: FragmentActivity): BaseBottomSheet? {
        return super.show(activity, InventoryFilterBottomSheet::class.java.simpleName)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomDialog = it as BottomSheetDialog
            val bottomSheet =
                (bottomDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?)
                    ?: return@setOnShowListener
            val displayMetrics = requireActivity().resources.displayMetrics
            val height = displayMetrics.heightPixels
            val maxHeight = (height * 0.99).toInt()
            BottomSheetBehavior.from(bottomSheet).peekHeight = maxHeight
        }
        return dialog
    }

    companion object {
        fun newInstance(
            mAssetType: ArrayList<PopupModel>,
            mPartList: ArrayList<PartModel>,
            mLocations: ArrayList<PopupModel>,
            filterApplyCallback: () -> Unit,
        ): InventoryFilterBottomSheet {
            val dialog = InventoryFilterBottomSheet(filterApplyCallback)
            dialog.mAssetType = mAssetType
            dialog.mPartList = mPartList
            dialog.mLocations = mLocations
            return dialog
        }
    }

    private fun getAssetCategories(assetTypeId: String) {
        val param = HashMap<String, Any>()
        param["AssetTypeId"] = assetTypeId

        viewModel.getAssetCategories(param).observe(this) {
            it.status.typeCall(
                success = {
                    headerView.headerProgressBar.gone()
                    resetCategory()
                    if (it.data != null && it.data.success) mAssetCategory.addAll(it.data.data)
                },
                error = { headerView.headerProgressBar.gone() },
                loading = { headerView.headerProgressBar.visible() }
            )
        }
    }

    private fun resetCategory() {
        binding.edtAssetCategory.setText(getString(R.string.all))
        binding.edtAssetCategory.tag = ""
        mAssetCategory.clear()
        mAssetCategory.add(PopupModel("", "All"))
    }
}