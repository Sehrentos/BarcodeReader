package com.nikohy.barcodereader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

private const val TAG = "JsonStore"

/**
 * **json.db** SQLite database
 */
class JsonDB(context: Context/*, lifecycle: Lifecycle*/) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION),
    DefaultLifecycleObserver {

    companion object {
        const val DATABASE_NAME = "json.db"
        const val DATABASE_VERSION = 1 // changing this will trigger onUpgrade
    }

    // create all tables
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(RecordsContract.CREATE_TABLE)
        //... more tables ...
        Log.d(TAG, "$DATABASE_NAME onCreate, added new table: ${RecordsContract.TABLE_NAME}")
    }

    // upgrade tables on version change
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "$DATABASE_NAME onUpgrade, $oldVersion < $newVersion")
        if (oldVersion < newVersion) {
            when (newVersion) {
                // 1 = initial version no upgrade needed
                2 -> Log.d(TAG, "$DATABASE_NAME onUpgrade, next version db upgrade query... todo")
                else -> Log.w(TAG, "$DATABASE_NAME onUpgrade, unspecified version: $oldVersion < $newVersion")
            }
        }
    }

    // region lifecycle events

    // used by DefaultLifecycleObserver to monitor parent lifecycle-events
    // https://developer.android.com/topic/libraries/architecture/lifecycle

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        Log.d("JsonDB", "Lifecycle::onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        Log.d("JsonDB", "Lifecycle::onStart")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        Log.d("JsonDB", "Lifecycle::onStop")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        Log.d("JsonDB", "Lifecycle::onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        Log.d("JsonDB", "Lifecycle::onPause")
    }

    // endregion

}
