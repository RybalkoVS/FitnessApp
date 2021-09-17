package com.example.fitnessapp.data.repository

import android.database.Cursor
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.data.database.Db
import com.example.fitnessapp.data.database.helpers.DeleteQueryBuilder
import com.example.fitnessapp.data.database.helpers.InsertQueryBuilder
import com.example.fitnessapp.data.database.helpers.SelectQueryBuilder
import com.example.fitnessapp.data.database.helpers.UpdateQueryBuilder
import com.example.fitnessapp.data.model.notification.Notification
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.point.PointDto
import com.example.fitnessapp.data.model.track.TrackDbo
import com.example.fitnessapp.data.model.track.TrackDto

class LocalRepository {

    companion object {
        private const val SELECT_ALL = "*"
        private const val SELECT_MAX_ID = "MAX(id) AS id"
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
            val trackId: Int
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().setTableName(name = Db.TRACK_TABLE_NAME)
                    .addSelectableField(field = Db.DB_ID)
                    .addWhereParam(name = Db.TRACK_SERVER_ID, value = serverId.toString())
                    .build(FitnessApp.INSTANCE.database)
                cursor.moveToFirst()
                trackId = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DB_ID))
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
                    .setTableName(Db.POINT_TABLE_NAME)
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

    fun updateTrack(track: TrackDbo): Task<Unit> {
        return Task.callInBackground {
            UpdateQueryBuilder().setTableName(name = Db.TRACK_TABLE_NAME)
                .addValueToUpdate(name = Db.TRACK_SERVER_ID, value = track.serverId.toString())
                .addWhereParam(name = Db.DB_ID, value = track.id.toString())
                .build(FitnessApp.INSTANCE.database)
        }
    }

    fun getNotifications(): Task<List<Notification>> {
        return Task.callInBackground {
            val notifications = mutableListOf<Notification>()
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().addSelectableField(SELECT_ALL)
                    .setTableName(Db.NOTIFICATIONS_TABLE_NAME)
                    .build(FitnessApp.INSTANCE.database)
                while (cursor.moveToNext()) {
                    val notification = Notification(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DB_ID)),
                        date = cursor.getLong(cursor.getColumnIndexOrThrow(Db.DATE))
                    )
                    notifications.add(notification)
                }
            } finally {
                cursor?.close()
            }
            return@callInBackground notifications
        }
    }

    fun insertNotification(date: Long): Task<Unit> {
        return Task.callInBackground {
            InsertQueryBuilder().setTable(name = Db.NOTIFICATIONS_TABLE_NAME)
                .addValueToInsert(fieldName = Db.DATE, value = date.toString())
                .build(db = FitnessApp.INSTANCE.database)
        }
    }

    fun updateNotification(notificationId: Int?, newDate: Long): Task<Unit> {
        return Task.callInBackground {
            UpdateQueryBuilder().setTableName(name = Db.NOTIFICATIONS_TABLE_NAME)
                .addValueToUpdate(name = Db.DATE, value = newDate.toString())
                .addWhereParam(name = Db.DB_ID, value = notificationId.toString())
                .build(FitnessApp.INSTANCE.database)
        }
    }

    fun getLastNotification(): Task<Notification> {
        return Task.callInBackground {
            var notification: Notification? = null
            var cursor: Cursor? = null
            try {
                cursor = SelectQueryBuilder().setTableName(name = Db.NOTIFICATIONS_TABLE_NAME)
                    .addSelectableField(field = SELECT_MAX_ID)
                    .build(FitnessApp.INSTANCE.database)
                if (cursor.moveToFirst()) {
                    val notificationId = cursor.getInt(cursor.getColumnIndexOrThrow(Db.DB_ID))
                    val notificationDate = getNotificationDateById(notificationId)
                    notification = Notification(notificationId, notificationDate)
                }
            } finally {
                cursor?.close()
            }
            return@callInBackground notification
        }
    }

    private fun getNotificationDateById(id: Int): Long {
        val notificationDate: Long
        var cursor: Cursor? = null
        try {
            cursor = SelectQueryBuilder().setTableName(name = Db.NOTIFICATIONS_TABLE_NAME)
                .addSelectableField(field = Db.DATE)
                .addWhereParam(name = Db.DB_ID, value = id.toString())
                .build(FitnessApp.INSTANCE.database)
            cursor.moveToFirst()
            notificationDate = cursor.getLong(cursor.getColumnIndexOrThrow(Db.DATE))
        } finally {
            cursor?.close()
        }
        return notificationDate
    }

    fun deleteNotification(notificationId: Int): Task<Unit> {
        return Task.callInBackground {
            DeleteQueryBuilder().setTableName(Db.NOTIFICATIONS_TABLE_NAME)
                .addWhereParam(name = Db.DB_ID, value = notificationId.toString())
                .build(FitnessApp.INSTANCE.database)
        }
    }

    fun clearDb() {
        Task.callInBackground {
            DeleteQueryBuilder().setTableName(Db.TRACK_TABLE_NAME)
                .build(FitnessApp.INSTANCE.database)
            DeleteQueryBuilder().setTableName(Db.POINT_TABLE_NAME)
                .build(FitnessApp.INSTANCE.database)
            DeleteQueryBuilder().setTableName(Db.NOTIFICATIONS_TABLE_NAME)
                .build(FitnessApp.INSTANCE.database)
        }
    }
}