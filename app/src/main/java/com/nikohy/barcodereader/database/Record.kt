package com.nikohy.barcodereader.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.serialization.Serializable

/**
 * Create new record row for table: [RecordsContract]
 *
 * JSON serialization:
 * val jsonString = Json.encodeToString(data)
 * val object = Json.decodeFromString<Data>(jsonString)
 */
@Serializable
data class Record(
    var id: Long = -1,
    var className: String = "",
    var content: String = "",
    var lifetime: LIFETIME = LIFETIME.INFINITE
) : DBRecord {

    override fun insert(db: SQLiteDatabase): Long {
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(RecordsContract.Columns.CLASS_NAME, className)
            put(RecordsContract.Columns.CONTENT, content)
            put(RecordsContract.Columns.LIFETIME, lifetime.ordinal)
        }
        // add row and update current ID
        id = db.insert(RecordsContract.TABLE_NAME, null, values)
        return id
    }

    override fun update(db: SQLiteDatabase): Long {
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(RecordsContract.Columns.CLASS_NAME, className)
            put(RecordsContract.Columns.CONTENT, content)
            put(RecordsContract.Columns.LIFETIME, lifetime.ordinal)
        }
        // add row and update current ID
        id = db.update(RecordsContract.TABLE_NAME, values, "${BaseColumns._ID}=?", arrayOf(id.toString())).toLong()
        return id
    }

    override fun delete(db: SQLiteDatabase): Boolean {
        db.execSQL("DELETE FROM  ${RecordsContract.TABLE_NAME} where ${BaseColumns._ID}='$id'")
        db.execSQL("VACUUM")
        return true
    }

    companion object {


    }
}