package com.example.fitnessapp.data.database.helpers

import android.database.sqlite.SQLiteDatabase

class DeleteQueryBuilder {

    companion object {
        private const val DELETE_FROM = "DELETE FROM"
        private const val EMPTY_STRING = ""
        private const val ERROR_MESSAGE = "Incorrect data"
    }

    private var tableName = EMPTY_STRING

    fun setTableName(name: String): DeleteQueryBuilder {
        tableName = name
        return this
    }

    fun build(db: SQLiteDatabase) {
        if (tableName != EMPTY_STRING) {
            db.execSQL("$DELETE_FROM $tableName")
        } else {
            error(ERROR_MESSAGE)
        }
    }
}