package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.nada.tech.R
import com.nada.tech.chainway_uhf.InventoryReadType.CONTINUES
import com.nada.tech.chainway_uhf.InventoryReadType.ONE_TIME
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.chainway_uhf.UHFTagScanActivity
import com.nada.tech.chainway_uhf.UhfInventoryCallback
import com.nada.tech.databinding.ActivityScanRfidBinding
import com.nada.tech.ui.adapter.ScanRFIDAdapter
import com.nada.tech.utility.Constants
import com.nada.tech.utility.Constants.EXTRA_TAG_LIST
import com.nada.tech.utility.Log
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanRFIDActivity : UHFTagScanActivity() {
    private lateinit var binding: ActivityScanRfidBinding
    private var adapter: ScanRFIDAdapter? = null
    private var tagList = ArrayList<UHFInventoryItem>()
    private var totalScanItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar("Scan RFID Tag", true)

        /*  val uHFInventoryItem=UHFInventoryItem()
          uHFInventoryItem.epc="787879797987823840923"
          tagList.add(uHFInventoryItem)*/
        binding.clickListener = this
        adapter = ScanRFIDAdapter(tagList)
        binding.recyclerView.adapter = adapter
        adapter?.onItemClickCallback = this::onItemClick
        binding.rbgScanType.setOnCheckedChangeListener { _, _ ->
            inventoryReadType = if (binding.rbContinue.isChecked) CONTINUES else ONE_TIME
        }

        iuhfInventoryCallback = inventoryItemCallback
//        updateCountUi()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnScan -> {
                if (isInventoryRunning) stopInventoryRead()
                else startInventoryRead()
                manageUi()
            }
            binding.btnApply -> {
                val intent = Intent()
                intent.putParcelableArrayListExtra(EXTRA_TAG_LIST, tagList)
                setResult(RESULT_OK, intent)
                finish()
            }
            binding.btnClear -> {
                if (isInventoryRunning) {
                    toast("Read inventory in-progress")
                    return
                }
                totalScanItem = 0
                tagList.clear()
                adapter?.notifyDataSetChanged()
                updateCountUi()
            }
        }
    }

    private fun manageUi() {
        if (isInventoryRunning) {
            binding.btnScan.text = getString(R.string.stop_scan)
            binding.rbgScanType.isEnabled = false
        } else {
            binding.btnScan.text = getString(R.string.start_scan)
            binding.rbgScanType.isEnabled = true
        }
    }

    private fun onItemClick(uhfInventoryItem: UHFInventoryItem?) {
        if (uhfInventoryItem == null) return
        setResult(Activity.RESULT_OK, intent.putExtra("SCANNED_ITEM", uhfInventoryItem))
        finish()
    }

    override fun onPause() {
        super.onPause()
        stopInventoryRead()
        manageUi()
    }

    private val inventoryItemCallback = object : UhfInventoryCallback {
        override fun onFoundItem(uhfInventoryItem: UHFInventoryItem) {
            totalScanItem += 1
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
            updateCountUi()
        }
    }

    private fun updateCountUi() {
        binding.txtTotalEpc.text = String.format("Total EPC : ${tagList.size}")
        binding.txtTotalCount.text = String.format("Total Scan Count : $totalScanItem")

        if (intent.hasExtra("SHOW_BOTTOM")) {
            binding.viewActionButton.gone()
            binding.linActionButton.gone()
        } else {
            if (tagList.isEmpty()) {
                binding.viewActionButton.gone()
                binding.linActionButton.gone()
            } else {
                binding.viewActionButton.visible()
                binding.linActionButton.visible()
            }
        }
    }

    /**
     * Handheld button event management
     * */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (event.repeatCount == 0) binding.btnScan.performClick()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}