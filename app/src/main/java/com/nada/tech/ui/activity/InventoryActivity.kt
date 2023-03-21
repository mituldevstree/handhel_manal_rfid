package com.nada.tech.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.nada.tech.R
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActivityInventoryBinding
import com.nada.tech.model.InventoryModel
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.pagination.PaginationHelper
import com.nada.tech.ui.adapter.InventoryAdapter
import com.nada.tech.ui.bottomsheet.InventoryDetailsBottomSheet
import com.nada.tech.ui.bottomsheet.InventoryFilterBottomSheet
import com.nada.tech.utility.Constants.SEARCH_REQUEST_DELAY
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InventoryActivity : ActionBarActivity() {

    private lateinit var binding: ActivityInventoryBinding

    private var mLocations = ArrayList<PopupModel>()
    private var mAssetType = ArrayList<PopupModel>()
    private var mPartList = ArrayList<PartModel>()

    @Inject
    lateinit var adapter: InventoryAdapter
    private val viewModel by viewModels<AssetViewModel>()
    private val filterMap = HashMap<String, Any>()

    private var paginationHelper: PaginationHelper<InventoryModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.inventory), true)
        actionView.layoutEnd.gone()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        adapter.onItemClickCallback = this::onItemClick

        paginationHelper = PaginationHelper(
            binding.recyclerView,
            layoutManager,
            binding.errorView,
            binding.progressBar,
            this::onNewPageCall
        )

        manageSearchView()

        getAssetType()
//        fetchLocations()
//        getPartNumbers()

        mAssetType.addAll(localDataHelper.assetType)
        mLocations.addAll(localDataHelper.locations)
        mPartList.addAll(localDataHelper.partList)

        filterMap["AssetTypeId"] = 0
        filterMap["AssetCategoryId"] = 0
        filterMap["PartId"] = 0
        filterMap["LocationId"] = 0
        filterMap["CheckIn"] = true
        filterMap["CheckOut"] = true

        inventoryObserver()
        paginationHelper?.refreshDataFromFirstPage()
    }

    private fun onItemClick(inventoryModel: InventoryModel?) {
        if (inventoryModel == null) return
        InventoryDetailsBottomSheet.newInstance(inventoryModel).show(this)
    }

    private var searchQuery = ""
    private fun manageSearchView() {
        actionView.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                if (searchQuery.isNotEmpty()) {
                    searchQuery = ""
                    forceSearch()
                }
            }

            override fun onSearchViewClosedAnimation() = Unit
            override fun onSearchViewShown() = Unit
            override fun onSearchViewShownAnimation() = Unit
        })

        actionView.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (searchQuery != newText) {
                    searchQuery = newText
                    searchInventory()
                }
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                searchQuery = ""
                forceSearch()
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchQuery = query
                forceSearch()
                return true
            }
        })
    }

    private var searchHandler: Handler? = null
    private val searchRunnable = Runnable { paginationHelper?.refreshDataFromFirstPage() }
    private fun searchInventory() {
        if (searchHandler == null) searchHandler = Handler(Looper.getMainLooper())
        else searchHandler?.removeCallbacks(searchRunnable)
        searchHandler?.postDelayed(searchRunnable, SEARCH_REQUEST_DELAY)
    }

    private fun forceSearch() {
        searchHandler?.removeCallbacks(searchRunnable)
        paginationHelper?.refreshDataFromFirstPage()
    }


    /**
     * Inventory data list
     * */
    private fun onNewPageCall(pageNumber: Int) {
        getInventoryData(pageNumber)
    }

    private fun getInventoryData(pageNumber: Int) {
        filterMap["Search"] = searchQuery
        filterMap["PageNumber"] = pageNumber
        filterMap["PageSize"] = PaginationHelper.PAGE_SIZE
        viewModel.getInventories(filterMap)
    }

    private fun inventoryObserver() {
        viewModel.inventoriesLiveData.observe(this) {
            it.status.typeCall(
                success = {
                    val data = it.data
                    if (data != null)
                        paginationHelper?.setSuccessResponse(data.success, data.data, data.message)
                    else paginationHelper?.setFailureResponse(it.message)
                },
                error = { paginationHelper?.setFailureResponse(it.message) },
                loading = {
                    paginationHelper?.handleErrorView("", GONE)
                    paginationHelper?.setProgressLayout(VISIBLE)
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.inventory_menu, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        actionView.searchView.setMenuItem(item)
        return true
    }

    private var filterBottomSheet: InventoryFilterBottomSheet? = null
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                if (filterBottomSheet == null) {
                    filterBottomSheet =
                        InventoryFilterBottomSheet.newInstance(
                            mAssetType,
                            mPartList,
                            mLocations,
                            this::onApplyFilter
                        )
                    filterBottomSheet?.filterMap = filterMap
                }
                filterBottomSheet?.show(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onApplyFilter() {
        paginationHelper?.refreshDataFromFirstPage()
    }

    override fun onBackPressed() {
        if (actionView.searchView.onBackPressed()) return
        super.onBackPressed()
    }

    /**
     * fetch data
     * */
    private fun fetchLocations() {
        viewModel.fetchLocation().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null && it.data.success) {
                        mLocations.clear()
                        mLocations.add(PopupModel("", "All"))
                        mLocations.addAll(it.data.data)
                    }
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
                    if (it.data != null) {
                        if (it.data.success) {
                            mPartList.clear()
                            mPartList.add(PartModel("", "All", "All"))
                            mPartList.addAll(it.data.data)
                        }
                    }
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
                    if (it.data != null) {
                        if (it.data.success) {
                            mAssetType.clear()
                            mAssetType.add(PopupModel("", "All"))
                            mAssetType.addAll(it.data.data)
                        }
                    }
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }
}