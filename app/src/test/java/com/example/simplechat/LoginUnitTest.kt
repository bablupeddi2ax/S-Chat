package com.example.simplechat

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginUnitTest {
    private lateinit var mAuth: FirebaseAuth
    @Test
    fun checkLoginFunction_withValidCredentials_returnsTrue() {
        val result = loginFunction("test@gmail.com", "Bablu@2002")
        assertEquals(true, result)
    }

    @Test
    fun checkLoginFunction_withInvalidCredentials_returnsFalse() {
        val result = loginFunction("invalidUser", "invalidPassword")
        assertEquals(false, result)
    }

    @Test
    fun checkLoginFunction_withEmptyCredentials_returnsFalse() {
        val result = loginFunction("", "")
        assertEquals(false, result)
    }

    private fun loginFunction(email: String, password: String): Boolean {
        // Implementation not shown
        var check = false
        mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                check = task.isSuccessful
            }
        return check
    }

}

