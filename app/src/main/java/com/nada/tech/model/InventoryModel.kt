package com.nada.tech.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class InventoryModel {

    @SerializedName("AssetType")
    var assetType: String? = null

    @SerializedName("AssetCategory")
    var assetCategory: String? = null

    @SerializedName("PartNumber")
    var partNumber: String? = null

    @SerializedName("PartName")
    var partName: String? = null

    @SerializedName("PartDescription")
    var partDescription: String? = null

    @SerializedName("AssetName")
    var assetName: String? = null

    @SerializedName("SerialNumber")
    var serialNumber: String? = null

    @SerializedName("ExpiryDate")
    var expiryDate: String? = null

    @SerializedName("AssetNotes")
    var assetNotes: String? = null

    @SerializedName("Location")
    var location: String? = null

    @SerializedName("IsMoveable")
    var isMoveable: String? = null

    @SerializedName("TagData")
    var tagData: String? = null

    @SerializedName("AssetStatus")
    var assetStatus: String? = null

    @SerializedName("ListAssetImages")
    var listAssetImages = ArrayList<AssetImages>()

    fun formattedAssetNotes(): String {
        if (assetNotes.isNullOrEmpty()) return "-"
        return assetNotes ?: "-"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as InventoryModel
        if (tagData != other.tagData) return false
        return true
    }

    override fun hashCode(): Int {
        return tagData?.hashCode() ?: 0
    }
}