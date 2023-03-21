package com.nada.tech.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.nada.tech.R
import com.nada.tech.chainway_uhf.InventoryReadType.CONTINUES
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.chainway_uhf.UHFTagScanActivity
import com.nada.tech.chainway_uhf.UhfInventoryCallback
import com.nada.tech.databinding.ActivityFindInventoryBinding
import com.nada.tech.ui.adapter.ScanRFIDAdapter
import com.nada.tech.utility.Log
import com.nada.tech.utility.decorator.DividerItemDecorator
import com.nada.tech.utility.gone
import com.nada.tech.utility.isVisible
import com.nada.tech.utility.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindInventoryActivity : UHFTagScanActivity(), (UHFInventoryItem) -> Unit {
    //    private var bottomSheetScannedItem: BottomSheetScannedItem? = null
    private lateinit var binding: ActivityFindInventoryBinding
    private var tagList = ArrayList<UHFInventoryItem>()
    private var adapter: ScanRFIDAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.find_item), true)
        if (intent.hasExtra("epc")) binding.edtEPC.setText(intent.getStringExtra("epc"))
        binding.clickListener = this
        inventoryReadType = CONTINUES
        isLocationMode = true
        iuhfInventoryCallback = inventoryItemCallback

        binding.recyclerView?.addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this,
            R.drawable.divider_decorator_gray)))
        adapter = ScanRFIDAdapter(tagList)
        adapter?.onItemClickCallback = this
        binding.recyclerView?.adapter = adapter
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnStartSearch -> {
                tagList.clear()
                adapter?.notifyDataSetChanged()
                hideKeyBoard()
                if (isInventoryRunning) stopInventoryRead()
                else startInventoryRead()
                manageUi()
            }
            binding.btnScan -> {
                hideKeyBoard()
                if (isInventoryRunning) stopInventoryRead()
                else startInventoryRead()
                updateSearchView(true)
                manageUi()
            }
        }
    }

    private fun manageUi() {
        if (binding.recyclerView!!.isVisible()) {
            if (isInventoryRunning) {
                binding.btnScan?.text = getString(R.string.stop_scan)
                binding.edtEPC.isEnabled = false
            } else {
                binding.btnScan?.text = getString(R.string.start_scan)
                binding.edtEPC.isEnabled = true
            }
        } else
            if (isInventoryRunning) {
                binding.imgFrequency.setImageResource(R.drawable.ic_wifi_no_signal)
                binding.btnStartSearch.text = getString(R.string.stop_search)
                binding.edtEPC.isEnabled = false
            } else {
                binding.btnStartSearch.text = getString(R.string.start_search)
                binding.edtEPC.isEnabled = true
            }
    }

    private fun setRangeIcon(rssi: Float) {
        when {
            rssi >= -50 -> { // Excellent
                Log.e("EPC Found : Excellent")
                setSoundConfig(1f, 1f, 300)
                binding.imgFrequency.setImageResource(R.drawable.ic_wifi_excellent_signal)
            }
            rssi < -50 && rssi >= -60 -> { // Good
                Log.e("EPC Found : Good")
                setSoundConfig(0.7f, 0.7f, 500)
                binding.imgFrequency.setImageResource(R.drawable.ic_wifi_good_signal)
            }
            rssi < -60 && rssi >= -70 -> { // Fair
                Log.e("EPC Found : Fair")
                setSoundConfig(0.5f, 0.5f, 700)
                binding.imgFrequency.setImageResource(R.drawable.ic_wifi_fair_signal)
            }
            else -> { // week
                Log.e("EPC Found : Week")
                setSoundConfig(0.3f, 0.3f, 900)
                binding.imgFrequency.setImageResource(R.drawable.ic_wifi_week_signal)
            }
        }
    }

    /*
    * Search EPC
    * */
    private val inventoryItemCallback = object : UhfInventoryCallback {
        override fun onFoundItem(uhfInventoryItem: UHFInventoryItem) {
            if (uhfInventoryItem.epc == binding.edtEPC.trim()) {
                Log.e("EPC Found : ${uhfInventoryItem.rssi}")
                setRangeIcon(uhfInventoryItem.rssi)
            } else {
                val index = tagList.indexOf(uhfInventoryItem)
                if (index != -1) {
                    Log.e("Duplicate: ${uhfInventoryItem.epc}")
                    uhfInventoryItem.count = tagList[index].count + 1
                    tagList[index] = uhfInventoryItem
                    adapter?.notifyItemChanged(index)
                } else {
                    Log.e("onFoundNewItem : ${uhfInventoryItem.epc}")
                    tagList.add(uhfInventoryItem)
                    adapter?.notifyItemInserted(tagList.size)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopInventoryRead()
        manageUi()
    }

    /**
     * Handheld button event management
     * */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (event.repeatCount == 0) {
                if (binding.imgFrequency.isVisible()) {
                    binding.btnStartSearch.performClick()
                } else {
                    binding.btnScan?.performClick()
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun invoke(p1: UHFInventoryItem) {
        binding.edtEPC.setText(p1.epc)
        stopInventoryRead()
        binding.btnScan!!.setText(R.string.start_scan)
        updateSearchView(false)
    }

    private fun updateSearchView(isList: Boolean) {
        if (isList) {
            binding.edtEPC.setText("")
            binding.recyclerView?.visible()
            binding.imgFrequency.gone()
            binding.btnStartSearch.isEnabled = false
        } else {
            binding.recyclerView?.gone()
            binding.imgFrequency.visible()
            binding.btnStartSearch.isEnabled = true
        }
    }
}