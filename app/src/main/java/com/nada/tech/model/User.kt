package com.nada.tech.model

import androidx.annotation.Keep

@Keep
data class User(
    val UserId: String,
    val UserName: String,
    val Email: String,
    val Phone: String,
    val AuthToken: String,
    val IsCreateAsset: Boolean = false,
    val IsManageAsset: Boolean = false,
    val IsCheckInOut: Boolean = false
)
