package com.example.fitnessapp.data.model.notification

import android.os.Parcel
import android.os.Parcelable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    var id: Int,
    val date: Long
) : Parcelable {

    val time: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(this.date)
            return sdf.format(date)
        }
    val dateInDateFormat: String
        get() {
            return DateFormat.getDateInstance().format(this.date)
        }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong()
    ) {
    }

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