package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActAddPartBinding
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.ui.bottomsheet.BottomSheetAssetCategory
import com.nada.tech.ui.bottomsheet.BottomSheetAssetType
import com.nada.tech.ui.bottomsheet.BottomSheetManufacturer
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.set

@AndroidEntryPoint
class AddPartActivity : ActionBarActivity() {

    private lateinit var binding: ActAddPartBinding

    private val viewModel by viewModels<AssetViewModel>()
    private var partModel: PartModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActAddPartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.add_part), true)
        binding.clickListener = this
        if (intent.hasExtra("data") && intent.getSerializableExtra("data") != null) {
            setUpToolbar(getString(R.string.update), true)
            partModel = intent.getSerializableExtra("data") as PartModel
            binding.partModel = partModel
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnCreate -> {
                if (validate()) {
                    hideKeyBoard()
                    addPart()
                }
            }
            binding.edtAssetType -> {
                BottomSheetAssetType.newInstance(
                    getString(R.string.asset_type),
                    true,
                    localDataHelper.assetType as ArrayList<PopupModel>,
                    object : BottomSheetAssetType.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtAssetType.setText(data.value)
                            binding.edtAssetType.tag = data.id
                        }
                    }).show(this)
            }
            binding.edtCategory -> {
                if (binding.edtAssetType.tag != null) {
                    getAssetCategories(binding.edtAssetType.tag.toString())
                }
            }
            binding.edtManufacture -> {
                BottomSheetManufacturer.newInstance(
                    getString(R.string.manufactor),true,
                    localDataHelper.manufacturer,
                    object : BottomSheetManufacturer.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtManufacture.setText(data.value)
                            binding.edtManufacture.tag = data.id
                        }
                    }).show(this)
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (binding.edtPartName.isEmpty) {
            binding.edtPartName.error = getString(R.string.please_enter_part_name)
            isValid = false
        }
        return isValid
    }

    /*
    * Asset registration api calling
    * */
    private fun addPart() {
        val requestParam = HashMap<String, Any>()
        requestParam["PartId"] = partModel?.partId ?: "0"
        requestParam["AssetTypeId"] = binding.edtAssetType.tag.toString()
        requestParam["AssetCategoryId"] = binding.edtCategory.tag.toString()
        requestParam["ManufacturerId"] = binding.edtManufacture.tag.toString()
        requestParam["Name"] = binding.edtPartName.text.toString()
        requestParam["Description"] = binding.edtDescription.text.toString()
        requestParam["IsExpire"] = binding.rdExpired.isChecked
        requestParam["IsSerial"] = binding.idSerial.isChecked
        viewModel.addpart(requestParam)
        viewModel.addPartLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    hideProgressDialog()
                    if (it.data != null) {
                        if (it.data.success) {
                            toast(it.data.message)
                            val partModel = it.data.data
                            val dataIntent = Intent()
                            dataIntent.putExtra("DATA", partModel)
                            setResult(Activity.RESULT_OK, dataIntent)
                            finish()
                        } else showAlertMessage(it.data.message)
                    }
                },
                error = {
                    hideProgressDialog()
                    showAlertMessage(it.message)
                },
                loading = {
                    showProgressDialog()
                },
            )
        }
    }

    private fun getAssetCategories(assetTypeId: String) {
        val param = java.util.HashMap<String, Any>()
        param["AssetTypeId"] = assetTypeId
        viewModel.getAssetCategories(param).observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    BottomSheetAssetCategory.newInstance(
                        getString(R.string.asset_category),
                        true,
                        it.data?.data as ArrayList<PopupModel>,
                        object : BottomSheetAssetCategory.BottomSheetItemClickListener {
                            override fun onItemClick(data: PopupModel) {
                                binding.edtCategory.setText(data.value)
                                binding.edtCategory.tag = data.id
                            }
                        }).show(this)
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }

}