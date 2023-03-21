package com.nada.tech.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.nada.tech.model.*
import com.nada.tech.network.Resource
import com.nada.tech.network.ResponseHandler
import com.nada.tech.network.ResponseHandler.baseError
import com.nada.tech.network.ResponseHandler.responseParser
import com.nada.tech.repository.AssetRepository
import com.nada.tech.utility.LocalDataHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val localDataHelper: LocalDataHelper,
    private val repository: AssetRepository,
) : BaseViewModel() {

    fun fetchLocation() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            responseParser(repository.fetchLocation(), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    fun getAssetType() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            responseParser(repository.getAssetType(), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    fun getManufacturer() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            responseParser(repository.getManufacturer(), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    fun getAssetCategories(params: HashMap<String, Any>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            responseParser(repository.getAssetCategories(params), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    fun getParts() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            responseParser(repository.getParts(), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    fun assetRegistration(
        params: HashMap<String, RequestBody>,
        photos: ArrayList<MultipartBody.Part>?,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            responseParser(repository.assetRegistration(params, photos), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    val inventoriesLiveData: MutableLiveData<Resource<ListBaseModel<InventoryModel>>> =
        MutableLiveData()

    fun getInventories(param: HashMap<String, Any>) {
        jobs["getInventories"]?.cancel()
        inventoriesLiveData.postValue(Resource.loading(null))

        addNewJob("getInventories", repository.getInventories(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) inventoriesLiveData.postValue(Resource.success(result))
                else inventoriesLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                inventoriesLiveData.postValue(
                    Resource.error(
                        data = null,
                        message = baseError(response).message
                    )
                )
            }
        })
    }


    val checkInCheckOutLiveData = MutableLiveData<Resource<BaseModel>>()

    fun checkInCheckOut(param: HashMap<String, Any>) {
        checkInCheckOutLiveData.postValue(Resource.loading(null))
        addNewJob("getInventories", repository.checkInCheckOut(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) checkInCheckOutLiveData.postValue(Resource.success(result))
                else checkInCheckOutLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                checkInCheckOutLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            }
        })
    }

    fun addLocation(param: HashMap<String, Any>) {
        checkInCheckOutLiveData.postValue(Resource.loading(null))
        addNewJob("addLocation", repository.addLocation(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) checkInCheckOutLiveData.postValue(Resource.success(result))
                else checkInCheckOutLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                checkInCheckOutLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            }
        })
    }
    val addPartLiveData = MutableLiveData<Resource<ObjectBaseModel<PartModel>>>()
    fun addpart(param: HashMap<String, Any>) {
        addPartLiveData.postValue(Resource.loading(null))
        addNewJob("addLocation", repository.addPart(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) addPartLiveData.postValue(Resource.success(result))
                else addPartLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                addPartLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            }
        })
    }

    fun addAssetType(param: HashMap<String, Any>) {
        checkInCheckOutLiveData.postValue(Resource.loading(null))
        addNewJob("addLocation", repository.addAssetType(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) addManufacturerLiveData.postValue(Resource.success(result))
                else addManufacturerLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                addManufacturerLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            }
        })
    }

    fun addCategory(param: HashMap<String, Any>) {
        addManufacturerLiveData.postValue(Resource.loading(null))
        addNewJob("addLocation", repository.addCategory(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) addManufacturerLiveData.postValue(Resource.success(result))
                else addManufacturerLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                addManufacturerLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            }
        })
    }
    val addManufacturerLiveData = MutableLiveData<Resource<ObjectBaseModel<PopupModel>>>()
    fun addManufacturer(param: HashMap<String, Any>) {
        checkInCheckOutLiveData.postValue(Resource.loading(null))
        addNewJob("addLocation", repository.addManufacturer(param, dispatcher) { response ->
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) addManufacturerLiveData.postValue(Resource.success(result))
                else addManufacturerLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            } else {
                addManufacturerLiveData.postValue(
                    Resource.error(data = null, message = baseError(response).message)
                )
            }
        })
    }
}