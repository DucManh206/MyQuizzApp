package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.auth.AuthScreen
import com.example.myapplication.database.auth.AuthDbHelper
import com.example.myapplication.database.crud.AuthDao
import com.example.myapplication.database.crud.QuestionDao
import com.example.myapplication.database.crud.QuizDao
import com.example.myapplication.database.quiz.QuizDbHelper
import com.example.myapplication.quiz.model.Quiz
import com.example.myapplication.quiz.ui.ListQuizScreen
import com.example.myapplication.quiz.ui.QuizDetailScreen
import com.example.myapplication.quiz.ui.QuizScreen
import com.example.myapplication.quiz.viewmodel.ListQuizManage
import com.example.myapplication.quiz.viewmodel.QuizManage

class MainActivity : ComponentActivity() {

    private inline fun <reified T : ViewModel> viewModel(crossinline create: () -> T): T =
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <V : ViewModel> create(modelClass: Class<V>): V = create() as V
            }
        )[T::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizDb = QuizDbHelper(applicationContext)
        val quizDao = QuizDao(quizDb)
        val questionDao = QuestionDao(quizDb)
        val authDb = AuthDbHelper(applicationContext)
        val authDao = AuthDao(authDb)

        val quizManage = viewModel { QuizManage(questionDao) }
        val listQuizManage = viewModel { ListQuizManage(quizDao) }

        setContent {
            MaterialTheme {
                var isAuthenticated by remember { mutableStateOf(false) }
                var selectedQuiz by remember { mutableStateOf<Quiz?>(null) }
                var inQuiz by remember { mutableStateOf(false) }

                when {
                    !isAuthenticated -> AuthScreen(authDao) { isAuthenticated = true }
                    inQuiz -> QuizScreen(quizManage)
                    selectedQuiz != null -> QuizDetailScreen(
                        quiz = selectedQuiz!!,
                        onStart = {
                            quizManage.loadQuiz(selectedQuiz!!.id)
                            inQuiz = true
                        },
                        onBack = { selectedQuiz = null }
                    )
                    else -> ListQuizScreen(listQuizManage) { selectedQuiz = it }
                }
            }
        }
    }
}
