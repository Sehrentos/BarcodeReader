package com.nikohy.barcodereader.database

/**
 * Lifetime enum table for database event life-cycle use cases
 */
enum class LIFETIME(val value: Int) {
    INFINITE(0),
    ON_CREATE(1),
    ON_START(2),
    ON_RESUME(3),
    ON_PAUSE(4),
    ON_STOP(5),
    ON_DESTROY(6);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}