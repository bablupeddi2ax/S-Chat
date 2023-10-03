@file:JvmName("FirebaseAuthService")
package com.example.simplechat.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


class FirebaseAuthService : AuthService {
    private val mAuth = FirebaseAuth.getInstance()

    override fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    override fun signUpWithEmailAndPasswordWithFCMToken(
        name: String,
        email: String,
        password: String,
        callback: (Boolean) -> Unit
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    var token :String
                    if (user != null) {
                        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                            if (fcmToken.isNotBlank() && !fcmToken.isNullOrBlank()) {
                                token = fcmToken
                                val fdb = FirebaseDatabaseService()
                                user.uid.let { uid ->
                                    fdb.addUserToDatabase(name, email, uid, token) { success ->
                                        callback(success) // Callback with success status
                                    }
                                }
                            } else {
                                callback(false) // Callback with failure status if token is blank
                            }
                        }
                    } else {
                        callback(false) // Callback with failure status if user is null
                    }
                } else {
                    callback(false) // Callback with failure status if user registration failed
                }
            }
    }
}
