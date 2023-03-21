package com.nada.tech.network

import com.nada.tech.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST(NetworkURL.LOGIN)
    suspend fun login(@Body params: HashMap<String, Any>?): Response<ObjectBaseModel<User>>

    @GET(NetworkURL.GET_PROFILE)
    suspend fun getProfile(): Response<ObjectBaseModel<User>>

    @GET(NetworkURL.GET_LOCATIONS)
    suspend fun getLocations(): Response<ListBaseModel<PopupModel>>

    @GET(NetworkURL.GET_ASSET_TYPES)
    suspend fun getAssetType(): Response<ListBaseModel<PopupModel>>
    @GET(NetworkURL.GET_MANUFACTURER)
    suspend fun getManufacturer(): Response<ListBaseModel<PopupModel>>

    @GET(NetworkURL.GET_ASSET_CATEGORY)
    suspend fun getAssetCategories(@QueryMap params: HashMap<String, Any>): Response<ListBaseModel<PopupModel>>

    @GET(NetworkURL.GET_PART)
    suspend fun getParts(): Response<ListBaseModel<PartModel>>

    @Multipart
    @POST(NetworkURL.ASSET_REGISTRATION)
    suspend fun assetRegistration(
        @PartMap params: HashMap<String, RequestBody>,
        @Part photos: ArrayList<MultipartBody.Part>?,
    ): Response<BaseModel>

    @POST(NetworkURL.INVENTORY_DATA)
    suspend fun getInventories(@Body params: HashMap<String, Any>): Response<ListBaseModel<InventoryModel>>

    @POST(NetworkURL.ADD_LOCATIONS)
    suspend fun addLocation(@Body params: HashMap<String, Any>): Response<BaseModel>

    @POST(NetworkURL.CREATE_PART)
    suspend fun addPart(@Body params: HashMap<String, Any>): Response<ObjectBaseModel<PartModel>>

    @POST(NetworkURL.CREATE_ASSET_TYPE)
    suspend fun addAssetType(@Body params: HashMap<String, Any>): Response<ObjectBaseModel<PopupModel>>

    @POST(NetworkURL.CREATE_ASSET_CATEGORY)
    suspend fun addCategory(@Body params: HashMap<String, Any>):  Response<ObjectBaseModel<PopupModel>>

    @POST(NetworkURL.CREATE_MANUFACTURER)
    suspend fun addManufacturer(@Body params: HashMap<String, Any>):Response<ObjectBaseModel<PopupModel>>

    @POST(NetworkURL.CHECK_IN_CHECK_OUT)
    suspend fun checkInCheckOut(@Body params: HashMap<String, Any>): Response<BaseModel>

}