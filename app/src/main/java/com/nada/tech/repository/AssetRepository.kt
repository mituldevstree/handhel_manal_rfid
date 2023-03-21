package com.nada.tech.repository

import com.nada.tech.model.*
import com.nada.tech.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AssetRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun fetchLocation() = apiService.getLocations()

    suspend fun getAssetType() = apiService.getAssetType()
    suspend fun getManufacturer() = apiService.getManufacturer()

    suspend fun getAssetCategories(params: HashMap<String, Any>) =
        apiService.getAssetCategories(params)

    suspend fun getParts() = apiService.getParts()

    suspend fun assetRegistration(
        params: HashMap<String, RequestBody>,
        photos: ArrayList<MultipartBody.Part>?,
    ) = apiService.assetRegistration(params, photos)

    fun getInventories(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<ListBaseModel<InventoryModel>>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.getInventories(params)
            responseCallback.invoke(response)
        }
    }


    fun addLocation(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<BaseModel>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.addLocation(params)
            responseCallback.invoke(response)
        }
    }

    fun addPart(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<ObjectBaseModel<PartModel>>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.addPart(params)
            responseCallback.invoke(response)
        }
    }

    fun addAssetType(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<ObjectBaseModel<PopupModel>>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.addAssetType(params)
            responseCallback.invoke(response)
        }
    }

    fun addCategory(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<ObjectBaseModel<PopupModel>>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.addCategory(params)
            responseCallback.invoke(response)
        }
    }

    fun addManufacturer(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<ObjectBaseModel<PopupModel>>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.addManufacturer(params)
            responseCallback.invoke(response)
        }
    }


    fun checkInCheckOut(
        params: HashMap<String, Any>,
        dispatcher: CoroutineContext,
        responseCallback: (response: Response<BaseModel>) -> Unit,
    ): Job {
        return CoroutineScope(dispatcher).launch {
            val response = apiService.checkInCheckOut(params)
            responseCallback.invoke(response)
        }
    }
}