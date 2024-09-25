package com.nikohy.barcodereader.database

import android.database.sqlite.SQLiteDatabase

/**
 * database record interface
 */
interface DBRecord {
    /**
     * Insert row to the table
     */
    fun insert(db: SQLiteDatabase): Long

    /**
     * Update row from the table
     */
    fun update(db: SQLiteDatabase): Long

    /**
     * Delete row from the table
     */
    fun delete(db: SQLiteDatabase): Boolean
}