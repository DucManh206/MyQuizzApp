package com.example.myapplication.quiz.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quiz.model.Quiz
import com.example.myapplication.quiz.viewmodel.ListQuizManage

@Composable
fun ListQuizScreen(viewModel: ListQuizManage, onSelectQuiz: (Quiz) -> Unit) {
    val quizzes by viewModel.quizzes.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Chọn quiz", style = MaterialTheme.typography.headlineMedium)
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text("Lỗi: $error")
            quizzes.isEmpty() -> Text("Chưa có quiz")
            else -> LazyColumn {
                items(quizzes) { quiz ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onSelectQuiz(quiz) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(quiz.title, style = MaterialTheme.typography.titleMedium)
                            Text("${quiz.questionCount} câu hỏi")
                        }
                    }
                }
            }
        }
    }
}
