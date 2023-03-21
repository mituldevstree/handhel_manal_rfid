package com.nada.tech.chainway_uhf

enum class InventoryReadType(val value: String) {
    ONE_TIME("one_time"),
    CONTINUES("continues");

    companion object {
        //Lookup table
        private val LOOKUP: MutableMap<String, InventoryReadType> = HashMap()

        //This method can be used for reverse lookup purpose
        operator fun get(value: String?): InventoryReadType {
            return try {
                if (value.isNullOrEmpty()) ONE_TIME
                else LOOKUP[value] ?: ONE_TIME
            } catch (e: Exception) {
                e.printStackTrace()
                ONE_TIME
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