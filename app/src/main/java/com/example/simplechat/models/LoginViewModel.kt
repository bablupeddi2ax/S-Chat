package com.example.simplechat.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplechat.Service.FirebaseAuthService
import com.example.simplechat.utils.ValidationResult

class LoginViewModel : ViewModel() {
    private val authService = FirebaseAuthService()
    val loginResult = MutableLiveData<ValidationResult>()
    fun login(email: String, password: String) {
        if (validate(email, password)) {
            authService.signInWithEmailAndPassword(email, password) { success ->
                if (success) {
                    // Successful login
                    loginResult.postValue(ValidationResult.Success("Login successful"))
                } else {
                    // Failed login
                    loginResult.postValue(ValidationResult.Error("Invalid credentials"))
                }
            }
        } else {
            // Validation failed
            loginResult.postValue(ValidationResult.Error("Invalid email or password"))
        }
    }

    private fun validate(email: String, password: String): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }
}
