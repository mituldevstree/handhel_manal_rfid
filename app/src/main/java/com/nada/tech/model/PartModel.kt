package com.nada.tech.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class PartModel() : Serializable {

    @SerializedName("AssetType")
    var assetType: String = ""

    @SerializedName("AssetTypeId")
    var assetTypeId: String = ""

    @SerializedName("AssetCategory")
    var assetCategory: String = ""

    @SerializedName("Manufacturer")
    var manufacturer: String = ""

    @SerializedName("PartId")
    var partId: String = ""

    @SerializedName("PartNumber")
    var partNumber: String = ""

    @SerializedName("PartName")
    var partName: String = ""

    @SerializedName("Description")
    var description: String = ""

    @SerializedName("IsExpire")
    var isExpire: Boolean = false

    @SerializedName("IsSerial")
    var isSerial: Boolean = false

    constructor(partId: String, partNumber: String, partName: String) : this() {
        this.partId = partId
        this.partNumber = partNumber
        this.partName = partName
    }
}