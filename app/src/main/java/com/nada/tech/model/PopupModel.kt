package com.nada.tech.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class PopupModel(
    @SerializedName(
        "id",
        alternate = ["LocationId", "AssetTypeId", "AssetCategoryId", "ManufacturerId"]
    )
    val id: String,

    @SerializedName("value", alternate = ["Location", "AssetType", "AssetCategory", "Manufacturer"])
    val value: String,
    @SerializedName("LocationCode", alternate = ["Code"])
    val LocationCode: String? = null,
) : Serializable
