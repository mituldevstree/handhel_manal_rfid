package com.nada.tech.common

import android.content.Intent
import androidx.activity.result.ActivityResult
import com.filepickersample.bottomsheet.AndroidFilePicker
import com.filepickersample.enumeration.FileSelectionType
import com.filepickersample.listener.FilePickerCallback
import com.nada.tech.BuildConfig
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import com.nada.tech.ui.activity.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class NavigationActivity : BaseActivity() {

    fun openHomeActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity()
    }

    fun openInventoryActivity() {
        val intent = Intent(this, InventoryActivity::class.java)
        startActivity(intent)
    }

    fun openAddLocationActivity(callBack: (result: ActivityResult) -> Unit, model: PopupModel?) {
        val intent = Intent(this, AddLocationActivity::class.java)
        intent.putExtra("data", model)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openAddPartActivity(callBack: (result: ActivityResult) -> Unit, model: PartModel?) {
        val intent = Intent(this, AddPartActivity::class.java)
        intent.putExtra("data", model)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openAddManufactorActivity(callBack: (result: ActivityResult) -> Unit, model: PartModel?) {
        val intent = Intent(this, AddManufactorActivity::class.java)
        intent.putExtra("data", model)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openAddAssetTypeActivity(callBack: (result: ActivityResult) -> Unit, model: PartModel?) {
        val intent = Intent(this, AddAssetTypeActivity::class.java)
        intent.putExtra("data", model)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openAddAssetCategoryActivity(
        callBack: (result: ActivityResult) -> Unit,
        model: PartModel?
    ) {
        val intent = Intent(this, AddAssetCategoryActivity::class.java)
        intent.putExtra("data", model)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openLocationActivity() {
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent)
    }

    fun openScanRFIDActivity(isShowBottom: Boolean, callBack: (result: ActivityResult) -> Unit) {
        val intent = Intent(this, ScanRFIDActivity::class.java)
        intent.putExtra("SHOW_BOTTOM", isShowBottom)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openScanRFIDActivity(callBack: (result: ActivityResult) -> Unit) {
        val intent = Intent(this, ScanRFIDActivity::class.java)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openScanRFIDActivity() {
        val intent = Intent(this, ScanRFIDActivity::class.java)
        startActivity(intent)
    }

    fun openFindInventoryActivity(epc: String? = "") {
        val intent = Intent(this, FindInventoryActivity::class.java)
        if (!epc.isNullOrEmpty()) intent.putExtra("epc", epc)
        startActivity(intent)
    }

    fun openPartListActivity(callBack: (result: ActivityResult) -> Unit) {
        val intent = Intent(this, PartListActivity::class.java)
        activityLauncher.launch(intent) { callBack.invoke(it) }
    }

    fun openAssetRegistrationActivity() {
        val intent = Intent(this, AssetRegistrationActivity::class.java)
        startActivity(intent)
    }

    fun openImageFilePicker(callback: FilePickerCallback) {
        AndroidFilePicker.with(BuildConfig.APPLICATION_ID)
            .type(FileSelectionType.IMAGE)
            .enableMultiSelection()
            .callBack(callback)
            .start(supportFragmentManager)
    }

}