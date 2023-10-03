package com.example.simplechat.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simplechat.R

import com.example.simplechat.service.FirebaseDatabaseService
import com.example.simplechat.utils.Utils
import com.example.simplechat.utils.validateEmail
import com.example.simplechat.utils.validateInputs
import com.example.simplechat.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.simplechat.service.FirebaseAuthService
/**
 * Features:
 *     User Registration with Firebase Authentication:
 *     Uses Firebase's createUserWithEmailAndPassword method for user registration.
 *
 *     Input Validation:
 *     Checks if the name, email, and password are not empty.
 *     Validates email  and password using  regular expressions.
 *
 *     Database Interaction:
 *     Adds the user's name, email, and UID to Firebase Realtime Database under the "users" node upon successful registration.
 *
 *     Navigation:
 *     On successful registration, the user is redirected to the MainActivity.
 *
 *     Code Organization:
 *     Input validation functions (validateEmail and validatePassword) are defined separately.
 *     A sealed class ValidationResult is used to represent validation results.
 **/
class Signup : AppCompatActivity() {
    // Define variables
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef:DatabaseReference
    private lateinit var utils: Utils
    private lateinit var mAuthService: FirebaseAuthService
    private lateinit var mDbService: FirebaseDatabaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //hide action supportActionBar
        supportActionBar?.hide()

        // initialize views
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnSignup = findViewById(R.id.btn_signup)

        //initialize auth and database reference
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        mAuthService = FirebaseAuthService()
        mDbService = FirebaseDatabaseService()


        utils = Utils()
        // set on click listener for signup button
        btnSignup.setOnClickListener{
            // remove extra spaces to avoid problems that might occur when user enter his details further
            // trim removes extra spaces at front and back of string
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val name = edtName.text.toString().trim()
            val emailValidation = validateEmail(email)
            val passwordValidation = validatePassword(password)

            // validate inputs before calling signup
            if (validateInputs(this,name, emailValidation, passwordValidation)) {
                // call signup method if inputs are valid
                signup(name,email, password)
            }
        }

    }

    // Handle user registration
    private fun signup(name: String, email: String, password: String) {
        mAuthService.signUpWithEmailAndPasswordWithFCMToken(name, email, password) { success:Boolean ->
            if (success) {
                // navigate to MainActivity
                utils.moveTo(this@Signup, MainActivity::class.java)

                // finish the activity so that the user cannot accidentally go back to signup
                finish()
            } else {
                // If sign-up fails, log an error message
                Log.i("signup", "signup_failure$email$password")
                Toast.makeText(this@Signup, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Validates user inputs

}



































/*
private fun signup(name:String,email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // add user to db if user is created successfully
                    addUserToDb(name,email,mAuth.currentUser?.uid!!)

                    // navigate to MainActivity
                    utils.moveTo(this@Signup, MainActivity::class.java)

                    //finish the activity so that user accidentally will not be able to redirected to signup again
                    finish()

                } else {
                    // If sign in fails, log an error message
                    Log.i("signup", "signup_failure$email$password")
                    Toast.makeText(this@Signup, "some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Adds user data to the Firebase Realtime Database
    private fun addUserToDb(name: String, email: String, uid: String) {

        //changed TODO check again for errors
        val userObject = User(name,email,uid,null)
        mDbRef.child("users").child(uid).setValue(User(name,email,uid,null))
        val user = FirebaseAuth.getInstance().currentUser
        var token = ""
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            if (it.isNotBlank() && !it.isNullOrBlank()) {
                // Store the FCM token in the database
                token =it
                mDbRef.child("users").child(user!!.uid).child("fcmToken").setValue(token)
            }
        }
        if (user != null && token!="") {
            val userId = user.uid
            // Store the FCM token in your database under the user's ID
            FirebaseDatabase.getInstance().reference.child("users").child(userId).child("fcmToken").setValue(token)
        }
    }
 */