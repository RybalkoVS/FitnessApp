package com.example.fitnessapp.data.database.helpers

import android.database.sqlite.SQLiteDatabase

class TableBuilder {

    companion object {
        private const val CREATE_TABLE = "CREATE TABLE"
        private const val EMPTY_STRING = ""
        private const val ERROR_MESSAGE = "Incorrect data"
    }

    private var tableName: String = EMPTY_STRING
    private var fields = mutableMapOf<String, String>()

    fun setName(name: String): TableBuilder {
        tableName = name
        return this
    }

    fun addField(fieldName: String, type: String): TableBuilder {
        fields[fieldName] = type
        return this
    }

    fun build(db: SQLiteDatabase?) {
        if (fields.isEmpty() || tableName == EMPTY_STRING) {
            error(message = ERROR_MESSAGE)
        } else {
            val fields = fields.entries.joinToString {
                "${it.key} ${it.value}"
            }
            db?.execSQL("$CREATE_TABLE $tableName ($fields)")
        }
    }
}