package com.example.myapplication.quiz.model

data class Question(
    val id: Int,
    val text: String,
    val optionIds: List<Int>,
    val options: List<String>,
    val correctOptionId: Int
)
