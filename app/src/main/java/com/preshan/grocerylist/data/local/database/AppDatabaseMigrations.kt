package com.preshan.grocerylist.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE shopping_session_items ADD COLUMN category_name_snapshot TEXT NOT NULL DEFAULT ''"
        )
        db.execSQL(
            """
            UPDATE shopping_session_items
            SET category_name_snapshot = (
                SELECT c.name FROM categories c
                WHERE c.id = shopping_session_items.category_id_snapshot
            )
            """.trimIndent()
        )
    }
}
