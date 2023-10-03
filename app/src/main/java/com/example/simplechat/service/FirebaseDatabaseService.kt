@file:JvmName("FirebaseAuthService")
package com.example.simplechat.service


import com.example.simplechat.models.User
import com.google.firebase.database.FirebaseDatabase


class FirebaseDatabaseService : DatabaseService {
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    override fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        fcmToken: String,
        callback: (Boolean) -> Unit
    ) {
        val user  = User(name,email,uid,fcmToken)
        dbRef.child(uid).setValue(user).addOnSuccessListener {
                    callback(true)
        }.addOnFailureListener {
            callback(false)
        }

    }

}
