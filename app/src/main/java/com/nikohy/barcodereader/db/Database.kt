package com.nikohy.barcodereader.db

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

const val DATABASE_NAME = "zdb"
const val DATABASE_VERSION = 1

/**
 * Sample SQLite database
 *
 * AndroidManifest: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 */
class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val tag = javaClass.simpleName

    init {
        try {
            val db = this.writableDatabase

            // backup check if tables are still empty
            if (!tableExists(db, MyTableContract.TABLE_NAME)) {
                Log.d(tag, "backup create tables")
                dropTable(db)
                createTable(db)
            }
        } catch (ex: Exception) {
            Log.e(tag, "init, exception, ${ex.message}")
        }
    }

    // Default event when creating database
    override fun onCreate(db: SQLiteDatabase) {
        try {
            // create tables into database
            db.execSQL(MyTableContract.CREATE_TABLE)
            Log.d(tag, "onCreate, added new table: ${MyTableContract.TABLE_NAME}")

            //... more tables ...

        } catch (ex: SQLException) {
            Log.e(tag, "error @ onCreate due to SQL string is invalid: $ex")
        } catch (ex: Exception) {
            Log.e(tag, "error @ onCreate: $ex")
        }
    }

    // Database upgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        try {
//            // If you need to add a column
//            if (newVersion > oldVersion) {
//                if (newVersion == 2) {
//                    db?.execSQL("ALTER TABLE ${MyTableContract.TABLE_NAME} ADD COLUMN ${MyTableContract.Columns.RESOURCE_ID} TEXT")
//                    db?.execSQL("ALTER TABLE ${MyTableContract.TABLE_NAME} ADD COLUMN ${MyTableContract.Columns.LOCATION_KEY} TEXT")
//                }
//                if (newVersion == 3) {
//                    db?.execSQL("ALTER TABLE ${MyTableContract.TABLE_NAME} ADD COLUMN ${MyTableContract.Columns.STATUS} INTEGER NOT NULL DEFAULT 0")
//                }
//            }
//        } catch (ex: SQLException) {
//            Log.e(tag, "Failed to upgrade table due to SQL string is invalid: $ex")
//        } catch (ex: Exception) {
//            Log.e(tag, "Failed to upgrade table due to: $ex")
//        }
    }

    //addRecord(MyTableContract.TABLE_NAME, values)
    fun addRecord(tblName: String, contentValues: ContentValues): Long {
        // 2nd argument is String containing nullColumnHack
        return this.writableDatabase.insert(tblName, null, contentValues)
    }

    // method to update data
    fun updateRecord(tblName: String, contentValues: ContentValues, id: String): Int {
        return this.writableDatabase.update(tblName, contentValues, "_id=?", arrayOf(id))
    }

    // method to delete data
    fun deleteRecord(tblName: String, id: String) {
        val db = this.writableDatabase

        // Delete record from table
        db.execSQL("DELETE FROM  $tblName where _id = '$id'")
        db.execSQL("VACUUM")

        //db.delete(tblName,"_id=?", arrayOf(id))
    }

    // method to get data
//    fun getRecord(query: String, vararg columns: String): Map<String, String> {
//        val db = this.writableDatabase
//        val retVal = mutableMapOf<String, String>()
//
//        db.rawQuery(query, null).use { cursor ->
//            if (cursor.moveToPosition(0)) {
//                for (column in columns) {
//                    cursor.getString(
//                        cursor.getColumnIndex(column)
//                    )?.let { retVal[column] = it }
//                }
//            }
//        }
//
//        return retVal
//    }

    // method to close connection
    fun closeDatabase() {
        this.writableDatabase.close()
    }

    fun tableExists(database: SQLiteDatabase, tableName: String): Boolean {
        database.rawQuery(
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'",
            null
        )?.use {
            return it.count > 0
        } ?: return false
    }

    fun createTable(db: SQLiteDatabase?) {
        try {
            Log.d(tag, "createTable, database is open -> ${writableDatabase.isOpen}")
            db?.execSQL(MyTableContract.CREATE_TABLE)
            Log.d(tag, "createTable, added new table: ${MyTableContract.TABLE_NAME}")
        } catch (ex: SQLException) {
            Log.e(tag, "error @ createTable due to SQL string is invalid: $ex")
        } catch (ex: Exception) {
            Log.e(tag, "error @ createTable: $ex")
        }
    }

    fun dropTable(db: SQLiteDatabase?) {
        try {
            db?.execSQL(MyTableContract.DROP_TABLE)
        } catch (ex: SQLException) {
            Log.e(tag, "error @ dropTable due to SQL string is invalid: $ex")
        } catch (ex: SQLiteException) {
            Log.e(tag, "error @ dropTable: $ex")
        } catch (ex: Exception) {
            Log.e(tag, "error @ dropTable: $ex")
        }
    }
}