package com.nada.tech.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.nada.tech.R
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActivityDashboardBinding
import com.nada.tech.listener.IDialogButtonClick
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.ui.bottomsheet.CheckInBottomSheet
import com.nada.tech.ui.bottomsheet.CheckOutBottomSheet
import com.nada.tech.ui.bottomsheet.MoveItemBottomSheet
import com.nada.tech.ui.dialog.DialogHelper
import com.nada.tech.ui.dialog.DialogProfile
import com.nada.tech.utility.Constants
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : ActionBarActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel by viewModels<AssetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.app_name), false)
        actionView.imgLogo.visibility = View.VISIBLE
        actionView.imgProfile.visibility = View.VISIBLE

        getAssetType()
        getManufacturer()
        fetchLocations()
        getPartNumbers()

        manageButtons()
    }

    private fun manageButtons() {
        val user = localDataHelper.user ?: return

        // check in/out
        if (user.IsCheckInOut) {
            binding.imgCheckIn.isSelected = true
            binding.txtCheckIn.isSelected = true
            binding.imgCheckOut.isSelected = true
            binding.txtCheckOut.isSelected = true
            binding.linCheckIn.setOnClickListener(this)
            binding.linCheckOut.setOnClickListener(this)
        } else {
            binding.imgCheckIn.isSelected = false
            binding.txtCheckIn.isSelected = false
            binding.imgCheckOut.isSelected = false
            binding.txtCheckOut.isSelected = false
        }

        // Asset registration
        if (user.IsCreateAsset) {
            binding.imgAssetRegistration.isSelected = true
            binding.txtAssetRegistration.isSelected = true
            binding.linAssetRegistration.setOnClickListener(this)
        } else {
            binding.imgAssetRegistration.isSelected = false
            binding.txtAssetRegistration.isSelected = false
        }

        // Inventory
        if (user.IsManageAsset) {
            binding.imgInventory.isSelected = true
            binding.txtInventory.isSelected = true
            binding.linInventory.setOnClickListener(this)
        } else {
            binding.imgInventory.isSelected = false
            binding.txtInventory.isSelected = false
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.linCheckIn -> {
                openScanRFIDActivity {
                    if (it.resultCode == RESULT_OK) {
                        if (it.data == null) return@openScanRFIDActivity
                        val newTagList: ArrayList<UHFInventoryItem>? =
                            it.data!!.getParcelableArrayListExtra(Constants.EXTRA_TAG_LIST)

                        if (newTagList.isNullOrEmpty()) return@openScanRFIDActivity
                        CheckInBottomSheet.newInstance(newTagList).show(this)
                    }
                }
            }
            binding.linMove -> {
                openScanRFIDActivity {
                    if (it.resultCode == RESULT_OK) {
                        if (it.data == null) return@openScanRFIDActivity
                        val newTagList: ArrayList<UHFInventoryItem>? =
                            it.data!!.getParcelableArrayListExtra(Constants.EXTRA_TAG_LIST)

                        if (newTagList.isNullOrEmpty()) return@openScanRFIDActivity
                        MoveItemBottomSheet.newInstance(newTagList).show(this)
                    }
                }
            }
            binding.linCheckOut -> {
                openScanRFIDActivity {
                    if (it.resultCode == RESULT_OK) {
                        if (it.data == null) return@openScanRFIDActivity
                        val newTagList: ArrayList<UHFInventoryItem>? =
                            it.data!!.getParcelableArrayListExtra(Constants.EXTRA_TAG_LIST)

                        if (newTagList.isNullOrEmpty()) return@openScanRFIDActivity
                        CheckOutBottomSheet.newInstance(newTagList).show(this)
                    }
                }
            }
            binding.linAssetRegistration -> {
                openAssetRegistrationActivity()
            }
            binding.linInventory -> {
                openInventoryActivity()
            }
            binding.linLocation -> {
                openLocationActivity()
            }

            binding.linFind -> openFindInventoryActivity()

            binding.linLogout -> {
                DialogHelper.newInstance(
                    getString(R.string.logout),
                    getString(R.string.are_you_sure_you_want_to_logout),
                    getString(R.string.logout),
                    getString(R.string.cancel),
                    object : IDialogButtonClick {
                        override fun onButtonClick(isPositive: Boolean) {
                            if (isPositive) {
                                logoutActions()
                                openLoginActivity()
                            }
                        }
                    }
                ).show(this)
            }
        }
    }

    override fun onProfileClick() {
        super.onProfileClick()
        DialogProfile.newInstance().show(this)
    }

    /**
     * fetch data
     * */
    private fun fetchLocations() {
        viewModel.fetchLocation().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null && it.data.success) localDataHelper.locations = it.data.data
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }

    private fun getPartNumbers() {
        viewModel.getParts().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null && it.data.success) localDataHelper.partList = it.data.data
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }

    private fun getAssetType() {
        viewModel.getAssetType().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null && it.data.success) localDataHelper.assetType = it.data.data
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }

    private fun getManufacturer() {
        viewModel.getManufacturer().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null && it.data.success) localDataHelper.manufacturer =
                        it.data.data as ArrayList<PopupModel>
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }
}