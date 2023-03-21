package com.nada.tech.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.nada.tech.databinding.DialogProfileBinding
import com.nada.tech.utility.LocalDataHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogProfile : BaseDialog(), View.OnClickListener {
    lateinit var binding: DialogProfileBinding

    @Inject
    lateinit var localDataHelper: LocalDataHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
        binding.user = localDataHelper.user

    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnNegative -> dismiss()
        }
    }

    override fun show(activity: FragmentActivity): BaseDialog? {
        return super.show(activity, DialogProfile::class.java.simpleName)
    }

    companion object {
        fun newInstance(): DialogProfile {
            return DialogProfile()
        }
    }
}