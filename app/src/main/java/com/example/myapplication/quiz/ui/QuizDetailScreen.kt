package com.example.myapplication.quiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quiz.model.Quiz

@Composable
fun QuizDetailScreen(quiz: Quiz, onStart: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(quiz.title, style = MaterialTheme.typography.headlineMedium)
        Text("${quiz.questionCount} câu hỏi")
        Text("Mỗi câu chọn một đáp án.")
        Button(onClick = onStart, enabled = quiz.questionCount > 0) {
            Text("Làm quiz")
        }
        TextButton(onClick = onBack) {
            Text("Quay lại")
        }
    }
}
