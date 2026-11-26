package com.example.myapplication.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.database.crud.AuthDao

@Composable
fun LoginScreen(dao: AuthDao, onLogin: () -> Unit, onRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Đăng nhập", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(username, { username = it }, label = { Text("Tên đăng nhập") }, singleLine = true)
        OutlinedTextField(password, { password = it }, label = { Text("Mật khẩu") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            if (dao.login(username, password.toCharArray())) onLogin()
            else message = "Tên đăng nhập hoặc mật khẩu không đúng"
        }) { Text("Đăng nhập") }
        TextButton(onClick = onRegister) { Text("Tạo tài khoản") }
    }
}
