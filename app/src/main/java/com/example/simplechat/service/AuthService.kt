//@file:JvmName("AuthService")
package com.example.simplechat.service


interface AuthService {
    fun signInWithEmailAndPassword(email: String, password: String, callback: (Boolean) -> Unit)


    fun signUpWithEmailAndPasswordWithFCMToken(
        name: String,
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    )
}
