package com.nada.tech.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ObjectBaseModel<T>(
    @SerializedName("Response") var data: T,
) :
    BaseModel(0, "Something went wrong")
