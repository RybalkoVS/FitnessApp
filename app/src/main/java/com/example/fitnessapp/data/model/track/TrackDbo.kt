package com.example.fitnessapp.data.model.track

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*


data class TrackDbo(
    var id: Int,
    var serverId: Int?,
    val beginTime: Long,
    val duration: Long,
    val distance: Int
) : Parcelable {

    val durationInMinutes: String
        get() {
            val seconds = this.duration / 1000 % 60
            val minutes = this.duration / (1000 * 60) % 60
            val hours = this.duration / (1000 * 60 * 60) % 24
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    val beginTimeDateFormat: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.getDefault())
            val date = Date(this.beginTime)
            return sdf.format(date)
        }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeValue(serverId)
        parcel.writeLong(beginTime)
        parcel.writeLong(duration)
        parcel.writeInt(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrackDbo> {
        override fun createFromParcel(parcel: Parcel): TrackDbo {
            return TrackDbo(parcel)
        }

        override fun newArray(size: Int): Array<TrackDbo?> {
            return arrayOfNulls(size)
        }
    }
}