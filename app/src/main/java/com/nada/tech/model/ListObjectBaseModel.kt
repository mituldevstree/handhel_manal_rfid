package com.nada.tech.model

import com.google.gson.annotations.SerializedName

class ListObjectBaseModel<K, T> : BaseModel(0, "Something went wrong") {
    @SerializedName("Response")
    val data: Map<K, List<T>>? = null
}
