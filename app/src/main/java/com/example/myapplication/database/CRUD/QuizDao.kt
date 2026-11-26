package com.example.myapplication.database.crud

import android.content.ContentValues
import com.example.myapplication.database.quiz.QuizDbHelper
import com.example.myapplication.quiz.model.Quiz

// Truy vấn bảng quiz.
class QuizDao(private val helper: QuizDbHelper) {

    fun loadQuizzes(): List<Quiz> {
        val quizzes = mutableListOf<Quiz>()
        helper.readableDatabase.rawQuery(
            "SELECT q.id, q.title, COUNT(qq.question_id) FROM quiz q " +
                "LEFT JOIN quiz_question qq ON qq.quiz_id = q.id GROUP BY q.id ORDER BY q.id",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) quizzes += Quiz(cursor.getInt(0), cursor.getString(1), cursor.getInt(2))
        }
        return quizzes
    }

    fun insertQuiz(title: String): Long =
        helper.writableDatabase.insertOrThrow("quiz", null, ContentValues().apply { put("title", title) })

    fun updateQuiz(id: Int, title: String): Boolean =
        helper.writableDatabase.update("quiz", ContentValues().apply { put("title", title) }, "id = ?", arrayOf(id.toString())) > 0

    fun deleteQuiz(id: Int): Boolean = helper.writableDatabase.let { db ->
        db.beginTransaction()
        try {
            db.delete("quiz_question", "quiz_id = ?", arrayOf(id.toString()))
            val deleted = db.delete("quiz", "id = ?", arrayOf(id.toString())) > 0
            db.setTransactionSuccessful()
            deleted
        } finally {
            db.endTransaction()
        }
    }

    fun linkQuestion(quizId: Int, questionId: Int) {
        helper.writableDatabase.execSQL(
            "INSERT OR IGNORE INTO quiz_question(quiz_id, question_id) VALUES (?, ?)",
            arrayOf(quizId, questionId)
        )
    }
}
