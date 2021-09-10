package com.example.fitnessapp.data.database.helpers

import android.database.sqlite.SQLiteDatabase

class InsertQueryBuilder {

    companion object {
        private const val INSERT_INTO = "INSERT INTO"
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
            val valuesString = values.entries.joinToString(SEPARATOR)
            db.execSQL("$INSERT_INTO $tableName $VALUES($valuesString)")
        }
    }

}