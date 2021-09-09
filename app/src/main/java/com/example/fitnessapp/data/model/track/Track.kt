package com.example.fitnessapp.data.model.track

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("id")
    val serverId: Int,
    @SerializedName("beginsAt")
    val beginTime: Long,
    @SerializedName("time")
    val duration: Long,
    @SerializedName("distance")
    val distance: Int,
    val isNotSent: Int = 1
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(serverId)
        parcel.writeLong(beginTime)
        parcel.writeLong(duration)
        parcel.writeInt(distance)
        parcel.writeInt(isNotSent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}