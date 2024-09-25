package com.nikohy.barcodereader.database

import android.provider.BaseColumns

object RecordsContract {
    const val TABLE_NAME = "records"

    // Table contents are grouped together in an anonymous object.
    object Columns : BaseColumns {
        const val CLASS_NAME = "className"
        const val CONTENT = "content"
        const val LIFETIME = "lifetime"
    }

    const val CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_NAME("
            + "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "${Columns.CLASS_NAME} TEXT, "
            + "${Columns.CONTENT} TEXT, "
            + "${Columns.LIFETIME} INTEGER)")

    const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}