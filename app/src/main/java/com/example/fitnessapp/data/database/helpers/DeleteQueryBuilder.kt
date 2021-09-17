package com.example.fitnessapp.data.database.helpers

import android.database.sqlite.SQLiteDatabase

class DeleteQueryBuilder {

    companion object {
        private const val DELETE_FROM = "DELETE FROM"
        private const val WHERE = "WHERE"
        private const val EMPTY_STRING = ""
        private const val ERROR_MESSAGE = "Incorrect data"
        private const val SEPARATOR = ","
    }

    private var tableName = EMPTY_STRING
    private val whereParams = mutableMapOf<String, String>()

    fun setTableName(name: String): DeleteQueryBuilder {
        tableName = name
        return this
    }

    fun addWhereParam(name: String, value: String): DeleteQueryBuilder {
        whereParams[name] = value
        return this
    }

    fun build(db: SQLiteDatabase) {
        val whereParamsString = whereParams.entries.joinToString(SEPARATOR)
        if (tableName == EMPTY_STRING) {
            error(ERROR_MESSAGE)
        } else {
            createSQLString(db, whereParamsString)
        }
    }

    private fun createSQLString(db:SQLiteDatabase, whereParams:String){
        if (whereParams == EMPTY_STRING) {
            db.execSQL("$DELETE_FROM $tableName")
        } else {
            db.execSQL("$DELETE_FROM $tableName $WHERE $whereParams")
        }
    }
}