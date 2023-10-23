// Set the JvmName for the file to "AuthService"
@file:JvmName("AuthService")

package com.example.simplechat.service

/**
 * AuthService interface defines authentication-related functions.
 */
interface AuthService {
    /**
     * Sign in with email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param callback Callback function called with a Boolean result indicating success.
     */
    fun signInWithEmailAndPassword(email: String, password: String, callback: (Boolean) -> Unit)

    /**
     * Sign up with email, password, and FCM token.
     *
     * @param name User's name.
     * @param email User's email address.
     * @param password User's password.
     * @param callback Callback function called with a Boolean result indicating success.
     */
    fun signUpWithEmailAndPasswordWithFCMToken(
        name: String,
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    )
}
