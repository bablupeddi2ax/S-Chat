@file:JvmName("DatabaseService")
package com.example.simplechat.service


interface DatabaseService {
    fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        fcmToken: String,
        callback: (Boolean) -> Unit
    )
}