package com.nikohy.barcodereader.database

//import android.provider.BaseColumns
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SQLite records table component that is lifecycle aware.
 *
 * Sample
 * val db = JsonDB(requireContext().applicationContext)
 * val records = RecordsTable(db)
 * lifecycle.addObserver(records)
 */
class RecordsTable(val db: JsonDB) : DefaultLifecycleObserver {

    /**
     * Try to convert given object using Gson library and return added row.
     *
     * Sample: val row = records.add(GoodsItem())
     */
    inline fun <reified T> add(obj: T, lifetime: LIFETIME = LIFETIME.INFINITE): Record {
        return Record(className = T::class.java.simpleName, content = Json.encodeToString(obj), lifetime = lifetime).apply {
            insert(db.writableDatabase)
        }
    }

    /**
     * Add new item to the db and return row
     *
     * Sample: val record = records.add("MyTest", Json.encodeToString(MyTest("abc")))
     */
//    fun add(name: String, json: String, lifetime: LIFETIME = LIFETIME.INFINITE): Record {
//        return Record(className = name, content = json, lifetime = lifetime).apply {
//            insert(db.writableDatabase)
//        }
//    }

    /**
     * get record by ID
     *
     * @param id Row id to search by
     *
     * sample: val record = records.getById("1")
     */
//    fun getById(id: String): Record? {
//        val query = "select * from ${RecordsContract.TABLE_NAME} where ${BaseColumns._ID}=? limit 1"
//
//        db.readableDatabase.rawQuery(query, arrayOf(id)).use { cursor ->
//            if (cursor.moveToFirst()) {
//                val res = Record(
//                    cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CLASS_NAME)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CONTENT)),
//                    LIFETIME.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow(RecordsContract.Columns.LIFETIME)))
//                )
//                cursor.close()
//                return res
//            }
//        }
//        return null
//    }

    /**
     * get record by ID and return specified class
     *
     * @param id Row id to search by
     *
     * sample: val myTest = records.getById<MyTest>("123")
     */
//    inline fun <reified T> getById(id: String): T? {
//        val query = "select * from ${RecordsContract.TABLE_NAME} where ${BaseColumns._ID}=? limit 1"
//
//        db.readableDatabase.rawQuery(query, arrayOf(id)).use { cursor ->
//            if (cursor.moveToFirst()) {
//                val content = cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CONTENT))
//                cursor.close()
//                return Json.decodeFromString<T>(content)
//            }
//        }
//        return null
//    }

    /**
     * get list of records by class name
     *
     * @param name class name eg. "GoodsItem"
     *
     * sample: val myTestList = records.getByName("MyTest")
     */
//    fun getByName(name: String): List<Record> {
//        val query = "select * from ${RecordsContract.TABLE_NAME} where ${RecordsContract.Columns.CLASS_NAME}=?"
//        val list = mutableListOf<Record>()
//
//        db.readableDatabase.rawQuery(query, arrayOf(name)).use { cursor ->
//            // get & append first position value
//            if (cursor.moveToFirst()) {
//                list.add(
//                    Record(
//                        cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CLASS_NAME)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CONTENT))
//                    )
//                )
//            }
//            // get & append next position values
//            while (cursor.moveToNext()) {
//                list.add(
//                    Record(
//                        cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CLASS_NAME)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CONTENT))
//                    )
//                )
//            }
//            cursor.close()
//        }
//        return list
//    }

    /**
     * get list of records by class type
     *
     * sample: val myTestList = records.getByClass<MyTest>()
     */
    inline fun <reified T> getByClass(): List<T> {
        val name = T::class.java.simpleName
        val query = "select * from ${RecordsContract.TABLE_NAME} where ${RecordsContract.Columns.CLASS_NAME}=?"
        val list = mutableListOf<T>()

        db.readableDatabase.rawQuery(query, arrayOf(name)).use { cursor ->
            // get & append first position value
            if (cursor.moveToFirst()) {
                list.add(
                    Json.decodeFromString(cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CONTENT)))
                )
            }
            // get & append next position values
            while (cursor.moveToNext()) {
                list.add(
                    Json.decodeFromString(cursor.getString(cursor.getColumnIndexOrThrow(RecordsContract.Columns.CONTENT)))
                )
            }
            cursor.close()
        }
        return list
    }

    /**
     * Delete record by ID
     */
//    fun delById(id: String): Boolean {
//        db.writableDatabase.apply {
//            execSQL("DELETE FROM ${RecordsContract.TABLE_NAME} where ${BaseColumns._ID} = '$id'")
//            execSQL("VACUUM")
//        }
//        return true
//    }

    /**
     * Delete records by className
     */
//    fun delByName(name: String): Boolean {
//        db.writableDatabase.apply {
//            execSQL("DELETE FROM ${RecordsContract.TABLE_NAME} where ${RecordsContract.Columns.CLASS_NAME} = '$name'")
//            execSQL("VACUUM")
//        }
//        return true
//    }

    /**
     * Delete records by class
     */
    inline fun <reified T> delByClass(): Boolean {
        val name = T::class.java.simpleName
        db.writableDatabase.apply {
            execSQL("DELETE FROM ${RecordsContract.TABLE_NAME} where ${RecordsContract.Columns.CLASS_NAME} = '$name'")
            execSQL("VACUUM")
        }
        return true
    }

    /**
     * Delete records by LIFETIME.
     *
     * Call these to activity life-cycle events manually.
     *
     * Sample Records.delByLifetime(LIFETIME.ON_DESTROY).let {
     * Log.d("TAG", "clear Records lifetime ON_DESTROY: $it")
     * }
     */
    fun delByLifetime(lifetime: LIFETIME): Boolean {
        db.writableDatabase.apply {
            execSQL("DELETE FROM ${RecordsContract.TABLE_NAME} where ${RecordsContract.Columns.LIFETIME} = ${lifetime.ordinal}")
            execSQL("VACUUM")
        }
        return true
    }

    // region LIFECYCLE EVENTS

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Log.d("RecordsTable", "Lifecycle::onCreate")
        removeExpiredLifetime(LIFETIME.ON_CREATE)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("RecordsTable", "Lifecycle::onStart")
        removeExpiredLifetime(LIFETIME.ON_START)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        removeExpiredLifetime(LIFETIME.ON_STOP)
        db.close()
        Log.d("RecordsTable", "Lifecycle::onStop closing db connection")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        removeExpiredLifetime(LIFETIME.ON_PAUSE)
        Log.d("RecordsTable", "Lifecycle::onPause")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        removeExpiredLifetime(LIFETIME.ON_RESUME)
        Log.d("RecordsTable", "Lifecycle::onResume")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        removeExpiredLifetime(LIFETIME.ON_DESTROY)
        Log.d("RecordsTable", "Lifecycle::onDestroy")
    }

    // endregion

    // region HELPERS

    private fun removeExpiredLifetime(lifetime: LIFETIME) {
        try {
            delByLifetime(lifetime)
        } catch (ex: Exception) {
            Log.e("RecordsTable", "removeExpiredLifetime has failed: ${ex.message}")
        }
    }

    // endregion

}