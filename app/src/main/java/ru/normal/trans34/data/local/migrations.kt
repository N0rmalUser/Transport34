package ru.normal.trans34.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS saved_routes (
                    id TEXT NOT NULL PRIMARY KEY,
                    title TEXT NOT NULL
                )
            """
        )
    }
}
