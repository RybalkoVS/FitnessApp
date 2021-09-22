package com.example.fitnessapp.data.model.notification

import android.os.Parcel
import android.os.Parcelable
import com.example.fitnessapp.DateTimeFormatter

data class Notification(
    var id: Int,
    val date: Long
) : Parcelable {

    val time: String
        get() = DateTimeFormatter.timeFormat.format(this.date)

    val dateInDateFormat: String
        get() = DateTimeFormatter.dateFormat.format(this.date)


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }

}