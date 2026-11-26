package com.example.myapplication.database.quiz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Chỉ quản schema. CRUD nằm trong database/crud/QuizDao.kt và QuestionDao.kt.
class QuizDbHelper(context: Context) :
    SQLiteOpenHelper(context, "quizapp.db", null, 3) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        QuizDbSeeder.seed(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) QuizDbSeeder.seed(db)
        if (oldVersion < 3) QuizDbSeeder.seed(db)
    }
}
