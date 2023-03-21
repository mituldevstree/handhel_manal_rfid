package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.gson.Gson
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActAddLocationBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.logging.Logger
import kotlin.collections.set

@AndroidEntryPoint
class AddManufactorActivity : ActionBarActivity() {

    private lateinit var binding: ActAddLocationBinding

    private val viewModel by viewModels<AssetViewModel>()
    private var popupModel: PopupModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.add_manufacturer), true)
        binding.tvInputLocation.hint = getString(R.string.code)
        binding.tvInputCode.hint = getString(R.string.name)
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
                    addManufacture()
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (binding.edtLocation.isEmpty) {
            binding.tvInputLocation.error = getString(R.string.name)
            isValid = false
        }
        if (binding.edtCode.isEmpty) {
            binding.tvInputCode.error = getString(R.string.code)
            isValid = false
        }

        return isValid
    }

    /*
    * Asset registration api calling
    * */
    private fun addManufacture() {
        val requestParam = HashMap<String, Any>()
        requestParam["ManufacturerId"] = popupModel?.id ?: "0"
        requestParam["Name"] = binding.edtCode.text.toString()
        requestParam["Code"] = binding.edtLocation.text.toString()
        viewModel.addManufacturer(requestParam)
        viewModel.addManufacturerLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    hideProgressDialog()
                    if (it.data != null) {
                        if (it.data.success) {
                            val tempList = localDataHelper.manufacturer
                            tempList.add(it.data.data)
                            localDataHelper.manufacturer = tempList
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