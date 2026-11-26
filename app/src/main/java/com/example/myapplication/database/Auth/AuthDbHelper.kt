package com.example.myapplication.database.auth

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Chỉ quản schema. CRUD nằm trong database/crud/AuthDao.kt.
class AuthDbHelper(context: Context) :
    SQLiteOpenHelper(context, "auth.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        AuthDbSeeder.seed(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        AuthDbSeeder.seed(db)
    }
}
