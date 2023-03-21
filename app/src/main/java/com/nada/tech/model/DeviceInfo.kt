package com.nada.tech.model

import android.os.Build

class DeviceInfo {
    var modelName: String = ""
    var deviceVersion: String = ""
    var manufacturer: String = ""

    companion object {
        fun get(): DeviceInfo {
            val info = DeviceInfo()
            info.modelName = Build.MODEL
            info.deviceVersion = Build.ID
            info.manufacturer = Build.MANUFACTURER
            return info
        }
    }
}