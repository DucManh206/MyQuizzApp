package com.example.myapplication.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.crud.QuizDao
import com.example.myapplication.quiz.model.Quiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ListQuizManage(private val dao: QuizDao) : ViewModel() {
    val quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val isLoading = MutableStateFlow(true)
    val error = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try { quizzes.value = dao.loadQuizzes() }
            catch (e: Exception) { error.value = e.message ?: "Không tải được danh sách quiz" }
            finally { isLoading.value = false }
        }
    }
}
