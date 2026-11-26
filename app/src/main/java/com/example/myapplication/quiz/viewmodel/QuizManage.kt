package com.example.myapplication.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.crud.QuestionDao
import com.example.myapplication.quiz.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuizManage(private val dao: QuestionDao) : ViewModel() {
    val questions = MutableStateFlow<List<Question>>(emptyList())
    val currentIndex = MutableStateFlow(0)
    val score = MutableStateFlow(0)
    val selectedAnswer = MutableStateFlow(-1)
    val finished = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun loadQuiz(quizId: Int) {
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                questions.value = dao.loadQuestions(quizId)
                currentIndex.value = 0
                score.value = 0
                selectedAnswer.value = -1
                finished.value = false
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Không tải được quiz"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun selectAnswer(index: Int) {
        val question = questions.value.getOrNull(currentIndex.value) ?: return
        if (selectedAnswer.value != -1 || index !in question.options.indices) return
        selectedAnswer.value = index
        if (question.optionIds[index] == question.correctOptionId) score.value++
    }

    fun nextQuestion() {
        if (selectedAnswer.value == -1) return
        if (currentIndex.value == questions.value.lastIndex) finished.value = true
        else {
            currentIndex.value++
            selectedAnswer.value = -1
        }
    }

    fun restartQuiz() {
        currentIndex.value = 0
        score.value = 0
        selectedAnswer.value = -1
        finished.value = false
    }
}
