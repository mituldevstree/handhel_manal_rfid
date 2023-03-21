package com.nada.tech.pagination

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nada.tech.databinding.LayoutErrorViewBinding
import com.nada.tech.network.ResponseHandler
import java.io.IOException
import java.net.UnknownHostException

class PaginationHelper<T>(
    private var recyclerView: RecyclerView,
    private var layoutManager: LinearLayoutManager,
    private var errorLayoutBinding: LayoutErrorViewBinding? = null,
    private var progress: View?,
    private val onNewPageCallBack: (pageNumber: Int) -> Unit,
) {

    private var isLoadingData = false
    private var canIncreasePageSize = true
    private var currentPageIndex = START_PAGE_INDEX
    private val arrayList = ArrayList<T?>()

    init {
        setPaginationRecycler()
    }

    private fun setPaginationRecycler() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                callOnScrollListener(dy)
            }
        })
    }

    fun resetValues() {
        isLoadingData = false
        canIncreasePageSize = true
        currentPageIndex = START_PAGE_INDEX
    }

    fun refreshDataFromFirstPage() {
        resetValues()
        arrayList.clear()
        notifyRecyclerView()
        onNewPageCallBack.invoke(START_PAGE_INDEX)
    }

    fun setProgressLayout(visibility: Int) {
        progress?.visibility = visibility.takeIf { currentPageIndex == START_PAGE_INDEX } ?: GONE
    }

    private fun callOnScrollListener(dy: Int) {
        val totalItem = layoutManager.itemCount
        val prevItem = layoutManager.findFirstVisibleItemPosition()
        val currentItem = layoutManager.childCount
        //Here the right side of || operator is used because when screen is too large and all 20 items are loaded then the pagination will never called. So its helps to load more data if there are available.
        if (dy > 0 && prevItem + currentItem >= totalItem && !isLoadingData || dy == 0 && arrayList.size > 0 && arrayList.size == currentItem && !isLoadingData) {
            if (canIncreasePageSize) {
                isLoadingData = true
                currentPageIndex += 1
                onNewPageCallBack.invoke(currentPageIndex)

            } else {
                if (arrayList.size > 0 && arrayList[arrayList.size - 1] != null) {
                    arrayList.add(null)
                    recyclerView.post { notifyRecyclerView() }

                }
                if (!recyclerView.canScrollVertically(1)) {
                    isLoadingData = true
                    onNewPageCallBack.invoke(currentPageIndex)
                }
            }
        }
    }

    fun setSuccessResponse(isSuccess: Boolean, arrayList1: List<T?>?, message: String) {
        setProgressLayout(GONE)
        handleErrorView(message, GONE)

        if (isSuccess) {
            isLoadingData = false
            canIncreasePageSize = true

            if (arrayList.size > 0 && arrayList[arrayList.size - 1] == null)
                arrayList.removeAt(arrayList.size - 1)

            if (arrayList1 != null)
                arrayList.addAll(arrayList1)

            if (arrayList.size < PAGE_SIZE)
                isLoadingData = true
            else arrayList.add(null)

            notifyRecyclerView()
        } else {
            if (arrayList.size > 0 && arrayList[arrayList.size - 1] == null) {
                arrayList.removeAt(arrayList.size - 1)
                notifyRecyclerView()
            }
        }

        if (currentPageIndex == START_PAGE_INDEX && arrayList.isEmpty()) {
            handleErrorView(message, VISIBLE)
        }
    }

    fun setFailureResponse(message: String) {
        setProgressLayout(GONE)

        if (currentPageIndex == START_PAGE_INDEX) handleErrorView(message, VISIBLE)
        else handleErrorView("", GONE)

        if (arrayList.size > 0 && arrayList[arrayList.size - 1] == null) {
            arrayList.removeAt(arrayList.size - 1)
            recyclerView.adapter?.notifyItemRemoved(arrayList.size)
        }
    }

    fun setFailureResponse(t: Throwable) {
        setProgressLayout(GONE)
        handleErrorView("", GONE)

        if (t is IOException || t is UnknownHostException) {
            //only for internet failure we are allow users to load more data if previous data available.
            //In other cases we don't need to load more data because suppose there is parse error then we don't need to allow user to load it again.
            if (arrayList.size > 0) {
                canIncreasePageSize = false
                isLoadingData = false
            }

            if (currentPageIndex == START_PAGE_INDEX) {
                handleErrorView(ResponseHandler.handleErrorResponse(t), VISIBLE)
            } else {
//                AlertMessage.showMessage(mContext, ResponseHandler.handleErrorResponse(t))
            }
        } else {
            if (currentPageIndex == START_PAGE_INDEX) {
                handleErrorView(ResponseHandler.handleErrorResponse(t), VISIBLE)
            }
        }

        if (arrayList.size > 0 && arrayList[arrayList.size - 1] == null) {
            arrayList.removeAt(arrayList.size - 1)
            notifyRecyclerView()
        }
    }

    fun handleErrorView(message: String, visibility: Int) {
        errorLayoutBinding?.txtErrorMsg?.visibility = visibility
        errorLayoutBinding?.txtErrorMsg?.text = message
    }

    @Suppress("UNCHECKED_CAST")
    fun notifyRecyclerView() {
        (recyclerView.adapter as PagedAdapter<*, T>).updateList(ArrayList(arrayList))
    }

    companion object {
        const val PAGE_SIZE = 10
        const val START_PAGE_INDEX = 1
    }
}
