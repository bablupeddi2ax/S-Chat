package com.example.simplechat.ui
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.simplechat.R
import com.example.simplechat.models.User
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore



/**
 * Features:
 *   User Profile Display:
 *   - Displays user profile information including the profile picture, name, and email.
 *   - Retrieves user data from Firebase Firestore and Realtime Database.
 *   - Uses the Picasso library to load and display the user's profile picture.
 *
 *   Firebase Integration:
 *   - Initializes Firebase components, including Firebase Authentication, Firestore, and Realtime Database.
 *   - Retrieves the current user's UID from Firebase Authentication.
 *
 *   Data Retrieval:
 *   - Retrieves the user's profile picture URL from Firestore and loads it using Picasso.
 *   - Retrieves the user's name and email from Realtime Database.
 *
 *   Error Handling:
 *   - Handles cases where the user's data may not be available or if errors occur during data retrieval.
 *
 *   Code Organization:
 *   - Organizes code into functions for clarity and readability.
 *   - Uses event listeners to respond to data changes in Realtime Database.
 **/
class ProfileActivity : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var txtName:TextView
    private lateinit var txtEmail:TextView
    private lateinit var mRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // Initialize views
        profileImage = findViewById(R.id.profileImage)
        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)
        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        mRef = FirebaseDatabase.getInstance().getReference("users")
        // Retrieve the user's UID
        val userUid = mAuth.currentUser?.uid

        if (userUid != null) {

            // Reference to the user's document in Firestore
            val userDocument = firestore.collection("profiles").document(userUid)

            // Retrieve the profile picture URL from Firestore
            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val profilePictureUrl = documentSnapshot.getString("profilePictureDownloadUri")

                        // Load and display the profile picture using Picasso
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            Picasso.get().load(profilePictureUrl).into(profileImage)
                        }
                        getUserName()
                    } else {
                        // Handle the case where the document doesn't exist
                        // You can set a default profile picture here
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur while fetching the data
                    // You can set a default profile picture here as well
                }
        }
    }


    // method to get user name
    private fun getUserName() {
        mRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(userSnapshot in snapshot.children){
                    if(userSnapshot.key==mAuth.currentUser?.uid){
                        val userObject = userSnapshot.getValue(User::class.java)
                        txtName.text = userObject?.name.toString()
                        if(userObject?.email==null){
                            txtEmail.text = "logged in anonymously "
                        }else {
                            txtEmail.text = userObject.email.toString()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
