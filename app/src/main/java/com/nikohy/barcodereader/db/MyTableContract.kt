package com.nikohy.barcodereader.db

import android.provider.BaseColumns

object MyTableContract {
    const val TABLE_NAME = "myTableName"

    // Table contents are grouped together in an anonymous object.
    object Columns : BaseColumns {
        const val WORKSHIFT_ID = "workshiftId"       // TR-tunnus, postitoimipaikkatunnus, pono + 3
        const val WORKSHIFT_KEY = "workshiftKey"
        const val DATASET_ID = "datasetId"          // Timepoint linetype
        const val NAME = "name"                     // Task name
        const val DESCRIPTION = "desc"             // Description of a task
        const val INSTRUCTIONS = "instructions"     // Instructions for task
        const val START_TIME = "startTime"          // Start time of a task
        const val END_TIME = "endTime"              // End time of a task
        const val SERIALIZED_SHIFT = "serializedShift"    // Serialized task
    }

    const val CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS ${TABLE_NAME}("
            + "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " // Declare identifier column
            + "${Columns.WORKSHIFT_ID} TEXT, "                // Workshift identifier for UI, not unique universally
            + "${Columns.WORKSHIFT_KEY} TEXT, "               // Workshift identifier for database
            + "${Columns.DATASET_ID} TEXT, "                  // Dataset id meaning terminal abbreviation, like OU
            + "${Columns.NAME} TEXT, "                        // Name of the workshift, like "Aamuvuoro". Propably not used anyway...
            + "${Columns.DESCRIPTION} TEXT, "                 // Description for the workshift to be displayed
            + "${Columns.INSTRUCTIONS} TEXT, "                // Instructions for the workshift
            + "${Columns.START_TIME} TEXT, "                  // Designed start time to be displayed
            + "${Columns.END_TIME} TEXT, "                    // Designed end time to be displayed
            + "${Columns.SERIALIZED_SHIFT} TEXT)")            // Workshift as serialized

    const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}