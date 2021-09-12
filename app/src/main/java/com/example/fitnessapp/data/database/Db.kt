package com.example.fitnessapp.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fitnessapp.data.database.helpers.TableBuilder

class Db(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "FitnessApp.db"
        const val TRACK_TABLE_NAME = "tracks"
        const val POINT_TABLE_NAME = "points"
        private const val INTEGER_NOT_NULL_PRIMARY_KEY_AUTOINCREMENT =
            "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT"
        private const val INTEGER = "INTEGER"
        private const val INTEGER_NOT_NULL = "INTEGER NOT NULL"
        private const val INTEGER_UNIQUE = "INTEGER UNIQUE"
        private const val LONG_NOT_NULL = "LONG NOT NULL"
        private const val REAL_NOT_NULL = "REAL NOT NULL"
        const val TRACK_SERVER_ID = "serverId"
        const val TRACK_ID = "trackId"
        const val DB_ID = "id"
        const val BEGIN_TIME = "beginTime"
        const val DURATION = "duration"
        const val DISTANCE = "distance"
        const val LATITUDE = "lat"
        const val LONGITUDE = "lng"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        buildTracksTable(db)
        buildPointsTable(db)
    }

    private fun buildTracksTable(db: SQLiteDatabase?) {
        TableBuilder().apply {
            setName(TRACK_TABLE_NAME)
            addField(fieldName = DB_ID, type = INTEGER_NOT_NULL_PRIMARY_KEY_AUTOINCREMENT)
            addField(fieldName = TRACK_SERVER_ID, type = INTEGER_UNIQUE)
            addField(fieldName = BEGIN_TIME, type = LONG_NOT_NULL)
            addField(fieldName = DURATION, type = LONG_NOT_NULL)
            addField(fieldName = DISTANCE, type = INTEGER_NOT_NULL)
            build(db)
        }
    }

    private fun buildPointsTable(db: SQLiteDatabase?) {
        TableBuilder().apply {
            setName(POINT_TABLE_NAME)
            addField(fieldName = DB_ID, type = INTEGER_NOT_NULL_PRIMARY_KEY_AUTOINCREMENT)
            addField(fieldName = TRACK_ID, type = INTEGER)
            addField(fieldName = LATITUDE, type = REAL_NOT_NULL)
            addField(fieldName = LONGITUDE, type = REAL_NOT_NULL)
            build(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO(no migrations)
    }
}