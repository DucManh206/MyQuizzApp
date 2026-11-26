package com.example.myapplication.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.database.crud.AuthDao

@Composable
fun RegisterScreen(dao: AuthDao, onRegistered: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Tạo tài khoản", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(username, { username = it }, label = { Text("Tên đăng nhập") }, singleLine = true)
        OutlinedTextField(password, { password = it }, label = { Text("Mật khẩu (ít nhất 8 ký tự)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            dao.register(username, password.toCharArray())
                .fold(onSuccess = { onRegistered() }, onFailure = { message = it.message })
        }) { Text("Đăng ký") }
    }
}
