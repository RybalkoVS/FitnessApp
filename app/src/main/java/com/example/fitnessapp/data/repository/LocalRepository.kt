package com.example.fitnessapp.data.repository

import android.database.Cursor
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.data.database.Db
import com.example.fitnessapp.data.database.helpers.InsertQueryBuilder
import com.example.fitnessapp.data.database.helpers.SelectQueryBuilder
import com.example.fitnessapp.data.model.track.Track

class LocalRepository {

    companion object {
        private const val SELECT_ALL = "*"
    }

    fun getTracks(): Task<MutableList<Track>> {
        return Task.callInBackground {
            val tracks = mutableListOf<Track>()
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().addSelectableField(SELECT_ALL)
                    .setTableName(Db.TRACK_TABLE_NAME)
                    .build(FitnessApp.INSTANCE.database)
                while (cursor.moveToNext()) {
                    val track = Track(
                        serverId = cursor.getInt(cursor.getColumnIndexOrThrow(Db.TRACK_SERVER_ID)),
                        beginTime = cursor.getLong(cursor.getColumnIndexOrThrow(Db.BEGIN_TIME)),
                        duration = cursor.getLong(cursor.getColumnIndexOrThrow(Db.DURATION)),
                        distance = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DISTANCE)),
                        isNotSent = cursor.getInt(cursor.getColumnIndexOrThrow(Db.IS_TRACK_SENT))
                    )
                    tracks.add(track)
                }
            } finally {
                cursor?.close()
            }
            return@callInBackground tracks
        }
    }

    fun saveTrack(track: Track) {
        Task.callInBackground {
            InsertQueryBuilder().setTable(name = Db.TRACK_TABLE_NAME)
                .addValueToInsert(fieldName = Db.TRACK_SERVER_ID, value = track.serverId.toString())
                .addValueToInsert(fieldName = Db.BEGIN_TIME, value = track.beginTime.toString())
                .addValueToInsert(fieldName = Db.DISTANCE, value = track.distance.toString())
                .addValueToInsert(fieldName = Db.DURATION, value = track.distance.toString())
                .build(db = FitnessApp.INSTANCE.database)
        }
    }
}