package com.example.myapplication.database.crud

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import android.util.Base64
import com.example.myapplication.database.auth.AuthDbHelper
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Truy vấn bảng users.
class AuthDao(private val helper: AuthDbHelper) {

    fun findPasswordHash(username: String): String? =
        helper.readableDatabase.rawQuery(
            "SELECT password_hash FROM users WHERE username = ?",
            arrayOf(username)
        ).use { if (it.moveToFirst()) it.getString(0) else null }

    fun insertUser(username: String, passwordHash: String): Boolean = try {
        helper.writableDatabase.insertOrThrow("users", null, ContentValues().apply {
            put("username", username)
            put("password_hash", passwordHash)
        }) != -1L
    } catch (_: SQLiteConstraintException) {
        false
    }

    fun updatePasswordHash(username: String, passwordHash: String): Boolean =
        helper.writableDatabase.update(
            "users",
            ContentValues().apply { put("password_hash", passwordHash) },
            "username = ?",
            arrayOf(username)
        ) > 0

    fun deleteUser(username: String): Boolean =
        helper.writableDatabase.delete("users", "username = ?", arrayOf(username)) > 0

    fun register(username: String, password: CharArray): Result<Unit> {
        val normalized = username.trim()
        if (!normalized.matches(Regex("[A-Za-z0-9_.-]{3,32}"))) {
            password.fill('\u0000')
            return Result.failure(IllegalArgumentException("Tên đăng nhập phải có 3–32 ký tự: chữ, số, _, . hoặc -"))
        }
        if (password.size < 8) {
            password.fill('\u0000')
            return Result.failure(IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự"))
        }
        val salt = ByteArray(16).also(SecureRandom()::nextBytes)
        val hash = try {
            hashPassword(password, salt)
        } finally {
            password.fill('\u0000')
        }
        val stored = "${Base64.encodeToString(salt, Base64.NO_WRAP)}:${Base64.encodeToString(hash, Base64.NO_WRAP)}"
        return if (insertUser(normalized, stored)) Result.success(Unit)
        else Result.failure(IllegalArgumentException("Tên đăng nhập đã tồn tại"))
    }

    fun login(username: String, password: CharArray): Boolean {
        val candidate = password
        try {
            val stored = findPasswordHash(username.trim()) ?: return false
            val parts = stored.split(":", limit = 2)
            if (parts.size != 2) return false
            val salt = Base64.decode(parts[0], Base64.NO_WRAP)
            val expected = Base64.decode(parts[1], Base64.NO_WRAP)
            return MessageDigest.isEqual(expected, hashPassword(candidate, salt))
        } finally {
            candidate.fill('\u0000')
        }
    }

    private fun hashPassword(password: CharArray, salt: ByteArray): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(password, salt, 120_000, 256))
            .encoded
}
