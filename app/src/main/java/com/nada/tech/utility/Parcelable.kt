package com.nada.tech.utility

import android.os.Parcel
import android.os.Parcelable

object Parcelable {
    inline fun <reified T : Enum<T>> Parcel.readEnum() =
        readString()?.let { enumValueOf<T>(it) }

    fun <T : Enum<T>> Parcel.writeEnum(value: T?) = writeString(value?.name)

    inline fun <reified T> parcelableCreator(
        crossinline create: (Parcel) -> T,
    ) = object : Parcelable.Creator<T> {
        override fun createFromParcel(source: Parcel) = create(source)
        override fun newArray(size: Int) = arrayOfNulls<T>(size)
    }
}