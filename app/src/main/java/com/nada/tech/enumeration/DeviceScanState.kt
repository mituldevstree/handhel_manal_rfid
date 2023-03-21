package com.nada.tech.enumeration

enum class DeviceScanState(val value: String) {
    NONE("none"),
    CHECK_IN("check-in"),
    CHECK_OUT("check-out"),
    INVENTORY("inventory"),
    FIND_ITEM("find");

    companion object {
        //Lookup table
        private val LOOKUP: MutableMap<String, DeviceScanState> = HashMap()

        //This method can be used for reverse lookup purpose
        operator fun get(value: String?): DeviceScanState {
            return try {
                if (value.isNullOrEmpty()) NONE
                else LOOKUP[value] ?: NONE
            } catch (e: Exception) {
                e.printStackTrace()
                NONE
            }
        }

        //Populate the lookup table on loading time
        init {
            for (type in values()) {
                LOOKUP[type.value] = type
            }
        }

    }
}