package com.example.simplechat.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.memoryCacheSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings
import com.google.firebase.ktx.Firebase

object FirebaseHelper {
    val database = FirebaseDatabase.getInstance().apply {
        setPersistenceEnabled(true)
    }
//    val settings = firestoreSettings {
//        // Use memory cache
//        setLocalCacheSettings(memoryCacheSettings {})
//        // Use persistent disk cache (default)
//        setLocalCacheSettings(persistentCacheSettings {})
//    }

    private val databaseReference  : DatabaseReference = database.reference
    private val userRef  : DatabaseReference = database.getReference("users")
    fun getDatabaseReference():DatabaseReference{
        return databaseReference
    }
    fun getUserReference():DatabaseReference{
        return userRef
    }
}