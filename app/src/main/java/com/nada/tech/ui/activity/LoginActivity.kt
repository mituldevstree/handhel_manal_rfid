package com.nada.tech.ui.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.updateLayoutParams
import com.google.android.material.internal.CheckableImageButton
import com.nada.tech.BuildConfig
import com.nada.tech.R
import com.nada.tech.common.NavigationActivity
import com.nada.tech.databinding.ActivityLoginBinding
import com.nada.tech.model.DeviceInfo
import com.nada.tech.network.NetworkURL.DEVICE_TYPE_ANDROID
import com.nada.tech.network.typeCall
import com.nada.tech.utility.Utils
import com.nada.tech.utility.toJson
import com.nada.tech.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : NavigationActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        binding.clickListener = this

        if (BuildConfig.DEBUG) {
            if (isTablet()) {
                binding.edtEmail.setText("ABC")
                binding.edtPassword.setText("123")
            } else {
                binding.edtEmail.setText("Sanjay")
                binding.edtPassword.setText("123")
            }
            login()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnLogin -> {
                hideKeyBoard()
                if (!validate()) return
                login()
            }
        }
    }

    private fun login() {
        showProgressDialog()
        getFcmToken { fcmToken, isSuccess ->
            if (isSuccess) {
                val param = HashMap<String, Any>()
                param["Username"] = binding.edtEmail.string
                param["Password"] = binding.edtPassword.string
                param["Puch_token"] = fcmToken
                param["Devicetype"] = DEVICE_TYPE_ANDROID
                param["Deviceid"] = Utils.getUniquePsuedoID()
                param["Deviceinfo"] = DeviceInfo.get().toJson()

                viewModel.login(param).observe(this) {
                    it.status.typeCall(
                        success = {
                            hideProgressDialog()
                            if (it.data != null) {
                                if (it.data.success) {
                                    localDataHelper.user = it.data.data
                                    localDataHelper.apiToken = it.data.data.AuthToken
                                    localDataHelper.login = true
                                    openHomeActivity()
                                } else {
                                    showAlertMessage(it.data.message)
                                }
                            }
                        },
                        error = {
                            hideProgressDialog()
                            showAlertMessage(it.message)
                        },
                        loading = {
                        }
                    )
                }

            } else {
                hideProgressDialog()
                showAlertMessage(fcmToken)
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (binding.edtEmail.isEmpty) {
            binding.tvInputEmail.error = getString(R.string.enter_user_name)
            isValid = false
        }

        if (binding.edtPassword.isEmpty) {
            binding.tvInputPassword.error = getString(R.string.enter_password)
            isValid = false
        }
        return isValid
    }

}