package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActAddLocationBinding
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.set

@AndroidEntryPoint
class AddLocationActivity : ActionBarActivity() {

    private lateinit var binding: ActAddLocationBinding

    private val viewModel by viewModels<AssetViewModel>()
    private var popupModel: PopupModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.add_location), true)
        binding.clickListener = this
        if (intent.hasExtra("data") && intent.getSerializableExtra("data") != null) {
            setUpToolbar(getString(R.string.update_location), true)
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
                    addLocation()
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (binding.edtLocation.isEmpty) {
            binding.tvInputLocation.error = getString(R.string.enter_location_name)
            isValid = false
        }
        if (binding.edtCode.isEmpty) {
            binding.tvInputCode.error = getString(R.string.enter_location_code)
            isValid = false
        }

        return isValid
    }

    /*
    * Asset registration api calling
    * */
    private fun addLocation() {
        val requestParam = HashMap<String, Any>()
        requestParam["LocationId"] = popupModel?.id ?: "0"
        requestParam["LocationCode"] = binding.edtCode.text.toString()
        requestParam["Location"] = binding.edtLocation.text.toString()
        viewModel.addLocation(requestParam)
        viewModel.checkInCheckOutLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    hideProgressDialog()
                    if (it.data != null) {
                        if (it.data.success) {
                            toast(it.data.message)
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