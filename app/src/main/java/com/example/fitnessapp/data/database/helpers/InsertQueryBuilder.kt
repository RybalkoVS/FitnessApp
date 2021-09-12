package com.example.fitnessapp.data.database.helpers

import android.database.sqlite.SQLiteDatabase

class InsertQueryBuilder {

    companion object {
        private const val INSERT_OR_IGNORE_INTO = "INSERT OR IGNORE INTO"
        private const val VALUES = "VALUES"
        private const val EMPTY_STRING = ""
        private const val ERROR_MESSAGE = "Incorrect data"
        private const val SEPARATOR = ","
    }

    private var tableName: String = EMPTY_STRING
    private var values = mutableMapOf<String, String>()

    fun setTable(name: String): InsertQueryBuilder {
        tableName = name
        return this
    }

    fun addValueToInsert(fieldName: String, value: String): InsertQueryBuilder {
        values[fieldName] = value
        return this
    }

    fun build(db: SQLiteDatabase) {
        if (tableName == EMPTY_STRING || values.isEmpty()) {
            error(ERROR_MESSAGE)
        } else {
            val filedNamesString = values.keys.joinToString(SEPARATOR)
            val fieldValuesString = values.values.joinToString(SEPARATOR)
            val updateString = values.entries.joinToString(SEPARATOR)
            db.execSQL("$INSERT_OR_IGNORE_INTO $tableName ($filedNamesString) $VALUES($fieldValuesString)")
        }
    }

}