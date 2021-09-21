package com.example.fitnessapp.data.model.track

import android.os.Parcel
import android.os.Parcelable
import com.example.fitnessapp.DependencyProvider


data class TrackDbo(
    var id: Int,
    var serverId: Int?,
    var beginTime: Long,
    var duration: Long,
    var distance: Int
) : Parcelable {

    val durationInMinutes: String
        get() {
            val seconds = this.duration / 1000 % 60
            val minutes = this.duration / (1000 * 60) % 60
            val hours = this.duration / (1000 * 60 * 60) % 24
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

    val beginTimeDateFormat: String
        get() = DependencyProvider.dateTimeFormatter.dateWithTimeFormat.format(this.beginTime)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt()
    )

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