package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActAddAssetCategoryBinding
import com.nada.tech.databinding.ActAddLocationBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.ui.bottomsheet.BottomSheetAssetType
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.set

@AndroidEntryPoint
class AddAssetCategoryActivity : ActionBarActivity() {

    private lateinit var binding: ActAddAssetCategoryBinding

    private val viewModel by viewModels<AssetViewModel>()
    private var popupModel: PopupModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActAddAssetCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.asset_category), true)
        binding.clickListener = this
        if (intent.hasExtra("data") && intent.getSerializableExtra("data") != null) {
            popupModel = intent.getSerializableExtra("data") as PopupModel
            binding.location = popupModel
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnCreate -> {
                if (validate()) {
                    hideKeyBoard()
                    addCategory()
                }
            }
            binding.edtAssetType -> {
                BottomSheetAssetType.newInstance(
                    getString(R.string.asset_type),true,
                    localDataHelper.assetType as ArrayList<PopupModel>,
                    object : BottomSheetAssetType.BottomSheetItemClickListener {
                        override fun onItemClick(data: PopupModel) {
                            binding.edtAssetType.setText(data.value)
                            binding.edtAssetType.tag = data.id
                        }
                    }).show(this)
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        /*      if (binding.edtLocation.isEmpty) {
                  binding.tvInputLocation.error = getString(R.string.asset_type)
                  isValid = false
              }*/
        if (binding.edtCategory.isEmpty) {
            binding.tvInputCategory.error = getString(R.string.asset_category)
            isValid = false
        }

        return isValid
    }

    /*
    * Asset registration api calling
    * */
    private fun addCategory() {
        val requestParam = HashMap<String, Any>()
        requestParam["AssetCategoryId"] = popupModel?.id ?: "0"
        requestParam["AssetTypeId"] = binding.edtAssetType.tag.toString()
        requestParam["AssetCategory"] = binding.edtCategory.text.toString()
        viewModel.addCategory(requestParam)
        viewModel.addManufacturerLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    hideProgressDialog()
                    if (it.data != null) {
                        if (it.data.success) {
                            toast(it.data.message)
                            val intent = Intent()
                            intent.putExtra("DATA", it.data.data)
                            setResult(Activity.RESULT_OK, intent)
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
}