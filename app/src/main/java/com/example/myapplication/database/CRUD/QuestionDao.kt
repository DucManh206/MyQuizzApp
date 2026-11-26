package com.example.myapplication.database.crud

import android.content.ContentValues
import com.example.myapplication.database.quiz.QuizDbHelper
import com.example.myapplication.quiz.model.Question

// Truy vấn bảng question / option / question_option.
class QuestionDao(private val helper: QuizDbHelper) {

    fun loadQuestions(quizId: Int): List<Question> =
        loadQuestions("WHERE id IN (SELECT question_id FROM quiz_question WHERE quiz_id = ?)", arrayOf(quizId.toString()))

    fun loadQuestions(): List<Question> = loadQuestions("", null)

    private fun loadQuestions(filter: String, args: Array<String>?): List<Question> {
        val questions = mutableListOf<Question>()
        val db = helper.readableDatabase
        db.rawQuery("SELECT id, text, correct_option_id FROM question $filter ORDER BY id", args).use { cursor ->
            while (cursor.moveToNext()) {
                val questionId = cursor.getInt(0)
                val optionIds = mutableListOf<Int>()
                val options = mutableListOf<String>()
                db.rawQuery(
                    "SELECT o.id, o.text FROM option o JOIN question_option qo ON qo.option_id = o.id " +
                        "WHERE qo.question_id = ? ORDER BY o.id",
                    arrayOf(questionId.toString())
                ).use { optionCursor ->
                    while (optionCursor.moveToNext()) {
                        optionIds.add(optionCursor.getInt(0))
                        options.add(optionCursor.getString(1))
                    }
                }
                questions += Question(questionId, cursor.getString(1), optionIds, options, cursor.getInt(2))
            }
        }
        return questions
    }

    fun countQuestions(): Int = helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM question", null)
        .use { if (it.moveToFirst()) it.getInt(0) else 0 }

    fun checkAnswer(questionId: Int, optionId: Int): Boolean =
        helper.readableDatabase.rawQuery(
            "SELECT 1 FROM question WHERE id = ? AND correct_option_id = ?",
            arrayOf(questionId.toString(), optionId.toString())
        ).use { it.moveToFirst() }

    fun insertQuestion(quizId: Int, text: String, answers: List<Pair<String, Boolean>>): Int =
        helper.writableDatabase.let { db ->
            require(answers.count { it.second } == 1) { "Cần đúng một đáp án đúng" }
            db.beginTransaction()
            try {
                val questionId = db.insertOrThrow("question", null, ContentValues().apply { put("text", text) }).toInt()
                answers.forEach { (answer, correct) ->
                    val optionId = db.insertOrThrow("option", null, ContentValues().apply { put("text", answer) }).toInt()
                    db.insertOrThrow("question_option", null, ContentValues().apply {
                        put("question_id", questionId)
                        put("option_id", optionId)
                    })
                    if (correct) {
                        db.update("question", ContentValues().apply { put("correct_option_id", optionId) }, "id = ?", arrayOf(questionId.toString()))
                    }
                }
                db.execSQL("INSERT OR IGNORE INTO quiz_question(quiz_id, question_id) VALUES (?, ?)", arrayOf(quizId, questionId))
                db.setTransactionSuccessful()
                questionId
            } finally {
                db.endTransaction()
            }
        }

    fun updateQuestionText(questionId: Int, text: String): Boolean =
        helper.writableDatabase.update("question", ContentValues().apply { put("text", text) }, "id = ?", arrayOf(questionId.toString())) > 0

    fun deleteQuestion(questionId: Int): Boolean = helper.writableDatabase.let { db ->
        db.beginTransaction()
        try {
            val optionIds = mutableListOf<Int>()
            db.rawQuery("SELECT option_id FROM question_option WHERE question_id = ?", arrayOf(questionId.toString()))
                .use { while (it.moveToNext()) optionIds += it.getInt(0) }
            db.delete("question_option", "question_id = ?", arrayOf(questionId.toString()))
            optionIds.forEach { db.delete("option", "id = ?", arrayOf(it.toString())) }
            db.delete("quiz_question", "question_id = ?", arrayOf(questionId.toString()))
            val deleted = db.delete("question", "id = ?", arrayOf(questionId.toString())) > 0
            db.setTransactionSuccessful()
            deleted
        } finally {
            db.endTransaction()
        }
    }
}
