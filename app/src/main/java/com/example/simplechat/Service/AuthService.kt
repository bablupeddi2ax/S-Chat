package com.example.simplechat.Service

interface AuthService {
    fun signInWithEmailAndPassword(email: String, password: String, callback: (Boolean) -> Unit)
}
