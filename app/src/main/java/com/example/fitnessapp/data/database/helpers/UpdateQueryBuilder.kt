package com.example.fitnessapp.data.database.helpers

import android.database.sqlite.SQLiteDatabase

class UpdateQueryBuilder {

    companion object {
        private const val UPDATE = "UPDATE"
        private const val SET = "SET"
        private const val WHERE = "WHERE"
        private const val EMPTY_STRING = ""
        private const val ERROR_MESSAGE = "Incorrect data"
        private const val SEPARATOR = ","
    }

    private var tableName = EMPTY_STRING
    private var set = mutableMapOf<String, String>()
    private var whereParams = mutableMapOf<String, String>()

    fun setTableName(name: String): UpdateQueryBuilder {
        tableName = name
        return this
    }

    fun addValueToUpdate(name: String, value: String): UpdateQueryBuilder {
        set[name] = value
        return this
    }

    fun addWhereParam(name: String, value: String): UpdateQueryBuilder {
        whereParams[name] = value
        return this
    }

    fun build(db: SQLiteDatabase) {
        if (tableName == EMPTY_STRING || set.isEmpty()) {
            error(ERROR_MESSAGE)
        } else {
            val setString = set.entries.joinToString(SEPARATOR)
            val whereParamsString = whereParams.entries.joinToString(SEPARATOR)
            if (whereParamsString == EMPTY_STRING) {
                db.execSQL("$UPDATE $tableName $SET $setString")
            } else {
                db.execSQL("$UPDATE $tableName $SET $setString $WHERE $whereParamsString")
            }
        }
    }
}