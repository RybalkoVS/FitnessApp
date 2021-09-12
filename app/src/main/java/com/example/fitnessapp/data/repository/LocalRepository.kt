package com.example.fitnessapp.data.repository

import android.database.Cursor
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.data.database.Db
import com.example.fitnessapp.data.database.helpers.InsertQueryBuilder
import com.example.fitnessapp.data.database.helpers.SelectQueryBuilder
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.point.PointDto
import com.example.fitnessapp.data.model.track.TrackDbo
import com.example.fitnessapp.data.model.track.TrackDto

class LocalRepository {

    companion object {
        private const val SELECT_ALL = "*"
    }

    fun getTracks(): Task<MutableList<TrackDbo>> {
        return Task.callInBackground {
            val tracks = mutableListOf<TrackDbo>()
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().addSelectableField(SELECT_ALL)
                    .setTableName(Db.TRACK_TABLE_NAME)
                    .build(FitnessApp.INSTANCE.database)
                while (cursor.moveToNext()) {
                    val track = TrackDbo(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DB_ID)),
                        serverId = cursor.getInt(cursor.getColumnIndexOrThrow(Db.TRACK_SERVER_ID)),
                        beginTime = cursor.getLong(cursor.getColumnIndexOrThrow(Db.BEGIN_TIME)),
                        duration = cursor.getLong(cursor.getColumnIndexOrThrow(Db.DURATION)),
                        distance = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DISTANCE))
                    )
                    tracks.add(track)
                }
            } finally {
                cursor?.close()
            }
            return@callInBackground tracks
        }
    }

    fun insertTrackList(tracks: List<TrackDto>): Task<Unit> {
        return Task.callInBackground {
            for (track in tracks) {
                InsertQueryBuilder().setTable(name = Db.TRACK_TABLE_NAME)
                    .addValueToInsert(
                        fieldName = Db.TRACK_SERVER_ID,
                        value = track.serverId.toString()
                    )
                    .addValueToInsert(
                        fieldName = Db.BEGIN_TIME,
                        value = track.beginTime.toString()
                    )
                    .addValueToInsert(
                        fieldName = Db.DISTANCE,
                        value = track.distance.toString()
                    )
                    .addValueToInsert(
                        fieldName = Db.DURATION,
                        value = track.duration.toString()
                    )
                    .build(db = FitnessApp.INSTANCE.database)
            }
        }
    }

    fun insertPointList(points: List<PointDto>, trackId: Int): Task<Unit> {
        return Task.callInBackground {
            for (point in points) {
                InsertQueryBuilder().setTable(name = Db.POINT_TABLE_NAME)
                    .addValueToInsert(fieldName = Db.TRACK_ID, value = trackId.toString())
                    .addValueToInsert(fieldName = Db.LONGITUDE, value = point.longitude.toString())
                    .addValueToInsert(fieldName = Db.LATITUDE, value = point.latitude.toString())
                    .build(db = FitnessApp.INSTANCE.database)
            }
        }
    }

    fun getTrackIdByServerId(serverId: Int): Task<Int> {
        return Task.callInBackground {
            var trackId = 0
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().setTableName(name = Db.TRACK_TABLE_NAME)
                    .addSelectableField(field = Db.DB_ID)
                    .addWhereParam(name = Db.TRACK_SERVER_ID, value = serverId.toString())
                    .build(FitnessApp.INSTANCE.database)
                cursor.moveToFirst()
                trackId = cursor.getInt(cursor.getColumnIndexOrThrow(Db.TRACK_SERVER_ID))
            } finally {
                cursor?.close()
            }
            return@callInBackground trackId
        }
    }

    fun getTrackPoints(trackId: Int): Task<MutableList<PointDbo>> {
        return Task.callInBackground {
            val points = mutableListOf<PointDbo>()
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().addSelectableField(SELECT_ALL)
                    .setTableName(Db.TRACK_TABLE_NAME)
                    .addWhereParam(name = Db.TRACK_ID, value = trackId.toString())
                    .build(FitnessApp.INSTANCE.database)
                while (cursor.moveToNext()) {
                    val point = PointDbo(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DB_ID)),
                        trackId = cursor.getInt(cursor.getColumnIndexOrThrow(Db.TRACK_ID)),
                        longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Db.LONGITUDE)),
                        latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Db.LATITUDE))
                    )
                    points.add(point)
                }
            } finally {
                cursor?.close()
            }
            return@callInBackground points
        }
    }

}