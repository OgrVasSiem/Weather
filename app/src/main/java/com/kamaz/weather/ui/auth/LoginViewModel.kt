package com.kamaz.weather.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamaz.weather.data.dataStore.AuthorizationDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authorizationDataStore: AuthorizationDataStore
) : ViewModel() {

    // Функция для сохранения логина и пароля
    fun saveCredentials(login: String, password: String) {
        viewModelScope.launch {
            val encryptedLogin = encrypt(login)
            val encryptedPassword = encrypt(password)
            authorizationDataStore.updateData { encryptedLogin to encryptedPassword }
        }
    }

    // Простое шифрование (Base64 + rotate)
    private fun encrypt(input: String): String {
        val base64 = android.util.Base64.encodeToString(input.toByteArray(), android.util.Base64.DEFAULT)
        return base64.reversed() // простая ротация символов
    }

    // Дешифровка (если понадобится)
    fun decrypt(input: String): String {
        val reversed = input.reversed()
        return String(android.util.Base64.decode(reversed, android.util.Base64.DEFAULT))
    }

    // Функция для выхода (очистка DataStore)
    fun clearCredentials() {
        viewModelScope.launch {
            authorizationDataStore.updateData { "" to "" }
        }
    }
}

