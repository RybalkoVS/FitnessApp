package com.example.fitnessapp.data.database.helpers

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SelectQueryBuilder {

    companion object {
        private const val SELECT = "SELECT"
        private const val FROM = "FROM"
        private const val WHERE = "WHERE"
        private const val EMPTY_STRING = ""
        private const val SEPARATOR = ","
    }

    private var fields = mutableListOf<String>()
    private var tableName: String = EMPTY_STRING
    private val whereParams = mutableMapOf<String, String>()

    fun addSelectableField(field: String): SelectQueryBuilder {
        fields.add(field)
        return this
    }

    fun setTableName(name: String): SelectQueryBuilder {
        tableName = name
        return this
    }

    fun addWhereParam(name: String, value: String): SelectQueryBuilder {
        whereParams[name] = value
        return this
    }

    fun build(db: SQLiteDatabase): Cursor {
        val selectableFields = fields.joinToString(SEPARATOR)
        val whereParams = whereParams.entries.joinToString(SEPARATOR)
        return if (whereParams == EMPTY_STRING) {
            db.rawQuery("$SELECT $selectableFields $FROM $tableName", null)
        } else {
            db.rawQuery("$SELECT $selectableFields $FROM $tableName $WHERE $whereParams", null)
        }
    }
}