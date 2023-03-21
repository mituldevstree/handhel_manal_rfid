package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActAddLocationBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.utility.gone
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.set

@AndroidEntryPoint
class AddAssetTypeActivity : ActionBarActivity() {

    private lateinit var binding: ActAddLocationBinding

    private val viewModel by viewModels<AssetViewModel>()
    private var popupModel: PopupModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.asset_type), true)
        binding.tvInputLocation.hint = getString(R.string.asset_type)
        binding.clickListener = this
        binding.tvInputCode.gone()
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
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (binding.edtLocation.isEmpty) {
            binding.tvInputLocation.error = getString(R.string.asset_type)
            isValid = false
        }

        return isValid
    }

    /*
    * Asset registration api calling
    * */
    private fun addCategory() {
        val requestParam = HashMap<String, Any>()
        requestParam["AssetTypeId"] = popupModel?.id ?: "0"
        requestParam["AssetType"] = binding.edtLocation.text.toString()
        viewModel.addAssetType(requestParam)
        viewModel.addManufacturerLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    hideProgressDialog()
                    if (it.data != null) {
                        if (it.data.success) {
                            val tempList = localDataHelper.assetType as ArrayList
                            tempList.add(it.data.data)
                            localDataHelper.assetType = tempList
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