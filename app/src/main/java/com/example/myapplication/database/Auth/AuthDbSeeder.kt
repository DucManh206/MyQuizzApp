package com.example.myapplication.database.auth

import android.database.sqlite.SQLiteDatabase

object AuthDbSeeder {
    fun seed(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL COLLATE NOCASE UNIQUE,
                password_hash TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}
