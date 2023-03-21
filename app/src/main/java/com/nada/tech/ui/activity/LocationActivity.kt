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
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.pagination.PaginationHelper
import com.nada.tech.ui.adapter.LocationAdapter
import com.nada.tech.utility.Constants.SEARCH_REQUEST_DELAY
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationActivity : ActionBarActivity() {
    private lateinit var binding: ActivityInventoryBinding
    private var mLocations = ArrayList<PopupModel>()

    @Inject
    lateinit var adapter: LocationAdapter
    private val viewModel by viewModels<AssetViewModel>()
    private var paginationHelper: PaginationHelper<PopupModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.locations), true)
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
        mLocations.addAll(localDataHelper.locations)

        inventoryObserver()
        paginationHelper?.refreshDataFromFirstPage()
    }

    private fun onItemClick(model: PopupModel?) {
        if (model == null) return
        openAddLocationActivity({
            inventoryObserver()
        }, model)
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
        paginationHelper?.setSuccessResponse(false,null,"")
    }


    private fun inventoryObserver() {
        paginationHelper?.resetValues()
        paginationHelper?.refreshDataFromFirstPage()
        viewModel.fetchLocation().observe(this) {
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
        menu.findItem(R.id.action_filter).isVisible = false
        menu.findItem(R.id.action_add).isVisible = true
        menu.findItem(R.id.action_search).isVisible = false
        actionView.searchView.setMenuItem(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                openAddLocationActivity({
                    inventoryObserver()
                }, null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (actionView.searchView.onBackPressed()) return
        super.onBackPressed()
    }
}