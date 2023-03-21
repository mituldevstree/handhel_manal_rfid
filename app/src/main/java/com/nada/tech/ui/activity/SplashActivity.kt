package com.nada.tech.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.nada.tech.common.NavigationActivity
import com.nada.tech.databinding.ActivitySplashBinding
import com.nada.tech.network.typeCall
import com.nada.tech.utility.LocalDataHelper
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import com.nada.tech.viewmodel.UserViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : NavigationActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val userViewModel by viewModels<UserViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        if (localDataHelper.login) {
            userViewModel.getProfile().observe(this) {
                it.status.typeCall(
                    success = {
                        binding.progressBar.gone()
                        if (it.data != null && it.data.success) {
                            localDataHelper.user = it.data.data
                        }
                        startApp()
                    },
                    error = {
                        binding.progressBar.gone()
                        startApp()
                    },
                    loading = {
                        binding.progressBar.visible()
                    }
                )
            }
        } else startApp()
    }

    private fun startApp() {
        if (localDataHelper.login) openHomeActivity()
        else openLoginActivity()
    }
}
