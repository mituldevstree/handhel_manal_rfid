package com.nada.tech.ui.activity

import android.app.Activity
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
import com.nada.tech.model.PartModel
import com.nada.tech.network.typeCall
import com.nada.tech.pagination.PaginationHelper
import com.nada.tech.ui.adapter.PartAdapter
import com.nada.tech.utility.Constants
import com.nada.tech.utility.Constants.SEARCH_REQUEST_DELAY
import com.nada.tech.utility.gone
import com.nada.tech.utility.visible
import com.nada.tech.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PartListActivity : ActionBarActivity() {

    private lateinit var binding: ActivityInventoryBinding

    @Inject
    lateinit var adapter: PartAdapter
    private val viewModel by viewModels<AssetViewModel>()
    private val filterMap = HashMap<String, Any>()

    private var paginationHelper: PaginationHelper<PartModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.part), true)
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
        getPartNumbers()
        paginationHelper?.refreshDataFromFirstPage()
    }

    private fun onItemClick(partModel: PartModel?) {
        if (partModel == null) return
        setResult(Activity.RESULT_OK, intent.putExtra(Constants.PART_DATA, partModel))
        finish()
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
//        getPartNumbers(
        paginationHelper?.setSuccessResponse(false,null,"")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.inventory_menu, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        menu.findItem(R.id.action_filter).isVisible = false
        menu.findItem(R.id.action_search).isVisible = false
        menu.findItem(R.id.action_add).isVisible = true
        actionView.searchView.setMenuItem(item)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                openAddPartActivity(
                    {
                        if (it.resultCode != Activity.RESULT_OK) return@openAddPartActivity
                        getPartNumbers()
                    }, null
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (actionView.searchView.onBackPressed()) return
        super.onBackPressed()
    }


    private fun getPartNumbers() {
        paginationHelper?.resetValues()
        viewModel.getParts().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    it.status.typeCall(
                        success = {
                            val data = it.data
                            if (data != null) {
                                paginationHelper?.setSuccessResponse(
                                    data.success,
                                    data.data,
                                    data.message
                                )
                            } else paginationHelper?.setFailureResponse(it.message)
                        },
                        error = { paginationHelper?.setFailureResponse(it.message) },
                        loading = {
                            paginationHelper?.handleErrorView("", GONE)
                            paginationHelper?.setProgressLayout(VISIBLE)
                        }
                    )
                },
                error = { actionView.toolbarProgressBar.gone() },
                loading = { actionView.toolbarProgressBar.visible() }
            )
        }
    }

}