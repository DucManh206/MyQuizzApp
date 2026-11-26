package com.example.myapplication.database.quiz

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

object QuizDbSeeder {

    fun seed(db: SQLiteDatabase) {
        createTables(db)
        seedQuizzes(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS quiz (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS quiz_question (
                quiz_id INTEGER NOT NULL REFERENCES quiz(id),
                question_id INTEGER NOT NULL REFERENCES question(id),
                PRIMARY KEY (quiz_id, question_id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS option (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS question (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL,
                correct_option_id INTEGER REFERENCES option(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS question_option (
                question_id INTEGER NOT NULL REFERENCES question(id),
                option_id INTEGER NOT NULL REFERENCES option(id),
                PRIMARY KEY (question_id, option_id)
            )
        """.trimIndent())
    }

    private fun seedQuizzes(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO quiz(id, title) VALUES (1, 'Kiến thức tổng hợp')")
        val count = db.rawQuery("SELECT COUNT(*) FROM question", null).use { it.moveToFirst(); it.getInt(0) }
        if (count == 0) seedQuestions(db)
        db.execSQL("INSERT OR IGNORE INTO quiz_question(quiz_id, question_id) SELECT 1, id FROM question")
    }

    private fun seedQuestions(db: SQLiteDatabase) {
        addQuestion(
            db,
            "Thủ đô của Việt Nam là gì?",
            listOf(
                "Hà Nội" to true,
                "Huế" to false,
                "Đà Nẵng" to false,
                "TP.HCM" to false
            )
        )

        addQuestion(
            db,
            "2 + 2 × 3 = ?",
            listOf(
                "12" to false,
                "8" to true,
                "10" to false,
                "6" to false
            )
        )

        addQuestion(
            db,
            "Ngôn ngữ lập trình chính của Android là?",
            listOf(
                "Java" to false,
                "Kotlin" to true,
                "Python" to false,
                "C#" to false
            )
        )

        addQuestion(
            db,
            "Hành tinh nào gần Mặt Trời nhất?",
            listOf(
                "Trái Đất" to false,
                "Sao Hỏa" to false,
                "Sao Thủy" to true,
                "Sao Kim" to false
            )
        )

        addQuestion(
            db,
            "Màu nào tạo từ đỏ + xanh dương?",
            listOf(
                "Vàng" to false,
                "Tím" to true,
                "Cam" to false,
                "Xanh lá" to false
            )
        )
    }

    private fun addQuestion(
        db: SQLiteDatabase,
        text: String,
        answers: List<Pair<String, Boolean>>
    ) {
        require(answers.count { it.second } == 1)

        val questionId = db.insertOrThrow(
            "question",
            null,
            ContentValues().apply { put("text", text) }
        )

        answers.forEach { (answer, correct) ->
            val optionId = db.insertOrThrow(
                "option",
                null,
                ContentValues().apply { put("text", answer) }
            )

            db.insertOrThrow(
                "question_option",
                null,
                ContentValues().apply {
                    put("question_id", questionId)
                    put("option_id", optionId)
                }
            )

            if (correct) {
                db.update(
                    "question",
                    ContentValues().apply { put("correct_option_id", optionId) },
                    "id = ?",
                    arrayOf(questionId.toString())
                )
            }
        }
    }
}
