package com.nada.tech.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
open class BaseModel(var code: Int = 0, @SerializedName("Message") var message: String = "") {
    @SerializedName("Status")
    val success: Boolean = false
}