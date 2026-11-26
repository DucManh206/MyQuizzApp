package com.example.myapplication.quiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.quiz.viewmodel.QuizManage

@Composable
fun QuizScreen(
    viewModel: QuizManage
) {
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val finished by viewModel.finished.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMessage != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lỗi: $errorMessage")
        }
        return
    }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Chưa có câu hỏi trong database")
        }
        return
    }

    val question = questions[currentIndex]
    val correctOptionIndex = question.optionIds.indexOf(question.correctOptionId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("QUIZ APP", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Câu ${currentIndex + 1}/${questions.size}", style = MaterialTheme.typography.titleMedium)
        Text("Điểm: $score", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Text(question.text, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        question.options.forEachIndexed { index, option ->
            val isCorrect = index == correctOptionIndex

            val backgroundColor = when {
                selectedAnswer == -1 -> MaterialTheme.colorScheme.primary
                isCorrect -> MaterialTheme.colorScheme.primary
                index == selectedAnswer -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val textColor = when {
                selectedAnswer != -1 && index != selectedAnswer && !isCorrect ->
                    MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onPrimary
            }

            Button(
                onClick = { viewModel.selectAnswer(index) },
                enabled = selectedAnswer == -1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = textColor,
                    disabledContainerColor = backgroundColor,
                    disabledContentColor = textColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.nextQuestion() },
            enabled = selectedAnswer != -1
        ) {
            Text(if (currentIndex == questions.lastIndex) "Xem điểm" else "Câu tiếp theo")
        }
    }

    if (finished) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Kết quả") },
            text = { Text("Bạn trả lời đúng $score/${questions.size} câu") },
            confirmButton = {
                TextButton(onClick = { viewModel.restartQuiz() }) {
                    Text("Làm lại")
                }
            }
        )
    }
}
