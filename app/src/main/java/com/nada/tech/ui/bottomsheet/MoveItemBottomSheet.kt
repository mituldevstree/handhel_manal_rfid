package com.nada.tech.ui.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nada.tech.R
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.databinding.BottomSheetCheckOutBinding
import com.nada.tech.databinding.BottomSheetMoveItemBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.utility.LocalDataHelper
import com.nada.tech.utility.requestBody
import com.nada.tech.utility.toJsonExcludeExpose
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoveItemBottomSheet : BaseHeaderBottomSheet() {

    private lateinit var binding: BottomSheetMoveItemBinding
    private val viewModel by viewModels<AssetViewModel>()
    private var mTagList = ArrayList<UHFInventoryItem>()
    private var mLocations = ArrayList<PopupModel>()

    @Inject
    lateinit var localDataHelper: LocalDataHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetMoveItemBinding.inflate(inflater, container, false)
        headerView = binding.headerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setExpanded(base?.isTablet() == true)
        dialog?.setCanceledOnTouchOutside(false)
        changeTitle(getString(R.string.move_item))
        initUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        binding.clickListener = this
        mLocations.addAll(localDataHelper.locations)
        binding.edtNote.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
        setUpObserver()
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnCheckIn -> {
                if (binding.edtLocation.tag == null) {
                    Toast.makeText(requireActivity(), "Select location", Toast.LENGTH_SHORT).show()
                    return
                }
                checkInInventory()
            }
            binding.edtLocation -> {
                locationBottomSheet()
            }
        }
    }

    private fun checkInInventory() {
        val param = HashMap<String, Any>()
        param["TransactionType"] = "Move"
        param["LocationId"] = binding.edtLocation.tag
        param["Notes"] = binding.edtNote.trim()
        param["TagDetail"] = mTagList
        viewModel.checkInCheckOut(param)
    }

    private fun locationBottomSheet() {
        if (mLocations.isEmpty()) {
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

    private fun setUpObserver() {
        viewModel.checkInCheckOutLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    base?.hideProgressDialog()
                    val data = it.data
                    if (data != null) {
                        base?.showAlertMessage(data.message)
                        { p0, p1 -> if (data.success) dismiss() }
                    } else {
                        base?.showAlertMessage(it.message)
                    }
                },
                error = {
                    base?.hideProgressDialog()
                    base?.showAlertMessage(it.message)
                },
                loading = {
                    base?.showProgressDialog()
                }
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        if (base?.isTablet() == true) return dialog

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

    override fun show(activity: FragmentActivity): BaseBottomSheet? {
        return super.show(activity, MoveItemBottomSheet::class.java.simpleName)
    }

    companion object {
        fun newInstance(mArrayList: ArrayList<UHFInventoryItem>): MoveItemBottomSheet {
            val fragment = MoveItemBottomSheet()
            fragment.mTagList = mArrayList
            return fragment
        }
    }
}