package com.nada.tech.chainway_uhf

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nada.tech.utility.Parcelable.parcelableCreator
import com.nada.tech.utility.float
import com.rscja.deviceapi.entity.UHFTAGInfo

class UHFInventoryItem() : Parcelable {
    var pc: String = ""

    @Expose
    @SerializedName("Tag")
    var epc: String = ""
    var tid: String = ""
    var rssi: Float = 0f
    var ant: String = ""
    var count: Int = 0

    constructor(parcel: Parcel) : this() {
        pc = parcel.readString().orEmpty()
        epc = parcel.readString().orEmpty()
        tid = parcel.readString().orEmpty()
        rssi = parcel.readFloat()
        ant = parcel.readString().orEmpty()
        count = parcel.readInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UHFInventoryItem
        if (tid != other.tid || epc != other.epc) return false
        return true
    }

    override fun hashCode(): Int {
        var result = epc.hashCode()
        result = 31 * result + tid.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pc)
        parcel.writeString(epc)
        parcel.writeString(tid)
        parcel.writeFloat(rssi)
        parcel.writeString(ant)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::UHFInventoryItem)

        fun get(uhftagInfo: UHFTAGInfo): UHFInventoryItem {
            val item = UHFInventoryItem()
            item.pc = uhftagInfo.pc
            item.epc = uhftagInfo.epc
            item.tid = uhftagInfo.tid
            item.rssi = uhftagInfo.rssi.float()
            item.ant = uhftagInfo.ant
            item.count = 1
            return item
        }
    }
}