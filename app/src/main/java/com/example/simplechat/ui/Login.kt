package com.example.simplechat.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.simplechat.R
import com.example.simplechat.utils.Utils
import com.example.simplechat.utils.ValidationResult
import com.example.simplechat.models.LoginViewModel
import com.example.simplechat.utils.FirebaseHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.math.BigInteger
import java.security.SecureRandom

/**
 * Features
 *     User Authentication (login used for existing users) with Firebase
 *     method: signInWithEmailAndPassword from firebase docs
 *     ref: https://firebase.google.com/docs/auth/android/start?hl=en&authuser=0
 *
 *     Input Validation
 *     checks if email and password ar not empty
 *     shows toast if any of them is empty
 *     shows toast if user does not exists with provided credentials
 *
 *     Navigation
 *         on successful validation user is navigated to next screen (MainActivity)
 *         otherwise a toast is shown
* */
class Login : AppCompatActivity() {
    // define variables
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var utils: Utils
    private lateinit var viewModel: LoginViewModel
    private lateinit var btnAnonymousLogin:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        utils = Utils()
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseAuth.getInstance()

        if(FirebaseAuth.getInstance().currentUser!=null){
           utils.moveTo(this@Login,MainActivity::class.java)

        }
        //hide action bar
        supportActionBar?.hide()

        // initialize views
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btn_login)
        btnSignup = findViewById(R.id.btn_signup)
        btnAnonymousLogin = findViewById(R.id.btnAnonymousLogin)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is ValidationResult.Success -> {
                    // Handle a successful login (e.g., navigate to the next screen)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    checkUserHasToken()
                    finish()

                }
                is ValidationResult.Error -> {
                    // Handle a failed login (e.g., display an error message)
                    utils.showToast("user does not exists!",this@Login)
                }
            }
        }
        // navigate to Signup Screen (user does not have an account)
        btnSignup.setOnClickListener {
            utils.moveTo(this@Login, Signup::class.java)
        }
        // validate and login with provided credentials
        btnLogin.setOnClickListener {
            // get email and password
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            // check if they are valid
            viewModel.login(email,password)
        }
        btnAnonymousLogin.setOnClickListener{
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signInAnonymously().addOnSuccessListener { authResult->

                utils.showToast("Signed in as guest",this@Login)
                checkUserHasToken()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            }.addOnFailureListener {e->
                Log.e(TAG,"Anonymous sign-in failed",e)
                utils.showToast("Anonymous sign-in failed.", this@Login)

            }
        }
    }

    // ...
    private fun checkUserHasToken() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userId = user.uid
            val databaseRef = FirebaseHelper.getDatabaseReference();

            // Check if the user has an FCM token in the database
            databaseRef.child("users").child(userId).child("fcmToken").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // If the FCM token doesn't exist, get it and store it
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                if (token.isNotBlank() && !token.isNullOrBlank()) {
                                    // Store the FCM token in the database
                                    databaseRef.child("users").child(userId).child("fcmToken").setValue(token)
                                    databaseRef.child("users").child(userId).child("name").setValue(generateRandomUserName(userId.length/4))
                                    databaseRef.child("users").child(userId).child("uid").setValue(userId)

                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any errors that may occur during the database operation
                    }
                }
            )
        }
    }

    private fun generateRandomUserName(length: Int): String {
        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(length / 2)
        secureRandom.nextBytes(randomBytes)
        val randomString = BigInteger(1, randomBytes).toString(16)
        // Ensure the generated string has the desired length
        return  "Guest"+randomString.padStart(length, '0')
    }


}

