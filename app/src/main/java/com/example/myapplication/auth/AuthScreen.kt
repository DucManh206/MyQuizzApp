package com.example.myapplication.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.database.crud.AuthDao

@Composable
fun AuthScreen(dao: AuthDao, onAuthenticated: () -> Unit) {
    var registering by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MaterialTheme {
            if (registering) {
                RegisterScreen(dao) { registering = false }
            } else {
                LoginScreen(dao, onLogin = onAuthenticated, onRegister = { registering = true })
            }
        }
    }
}
