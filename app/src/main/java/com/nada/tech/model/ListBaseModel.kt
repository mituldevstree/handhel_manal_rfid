package com.nada.tech.model

import com.google.gson.annotations.SerializedName

data class ListBaseModel<T>(
    @SerializedName("Response")
    var data: List<T>,
) : BaseModel(0, "Something went wrong")