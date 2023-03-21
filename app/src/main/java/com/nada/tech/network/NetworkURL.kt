package com.nada.tech.network

object NetworkURL {
    // network constants
    const val DEVICE_TYPE_ANDROID = "Android"
    const val DEVICE_TYPE_IOS = "iOS"

    //headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val QUERY_AUTHORIZATION = "api_token"
    const val BEARER = "Bearer"
    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded"

    // response code
    const val RESPONSE_OK = 200
    const val RESPONSE_CREATED = 201
    const val RES_NOT_FOUND = 404
    const val RES_UNAUTHORISED = 401
    const val RES_BLOCKED = 402
    const val RES_FORBIDDEN = 403
    const val RES_UNPROCESSABLE_ENTITY = 422
    const val RES_SERVER_ERROR = 500

    const val LOGIN = "api/User/UserLogin"
    const val GET_PROFILE = "api/Master/GetUserProfile"
    const val GET_PART = "api/Master/GetPart"
    const val GET_LOCATIONS = "api/Master/GetLocation"
    const val ADD_LOCATIONS = "/api/Master/SetLocation"
    const val GET_ASSET_TYPES = "api/Master/GetAssetType"
    const val GET_MANUFACTURER = "api/Master/GetManufacturer"
    const val CREATE_PART = "api/Master/SetPart"
    const val CREATE_ASSET_TYPE = "api/Master/SetAssetType"
    const val CREATE_ASSET_CATEGORY = "api/Master/SetAssetCategory"
    const val CREATE_MANUFACTURER = "api/Master/SetManufacturer"
    const val GET_ASSET_CATEGORY = "api/Master/GetAssetCategory"
    const val ASSET_REGISTRATION = "api/Master/SetAssetRegistration"
    const val INVENTORY_DATA = "api/Report/InventoryData"
    const val CHECK_IN_CHECK_OUT = "api/Transaction/SetCheckinCheckout"
}