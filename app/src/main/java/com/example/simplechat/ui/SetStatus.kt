package com.example.simplechat.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.simplechat.R
import com.example.simplechat.models.Status
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class SetStatus : AppCompatActivity() {
    private lateinit var edtStatusText: EditText
    private lateinit var imgStatusImage: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnDone: Button
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var selectedImageUri: Uri


        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_status)

        edtStatusText = findViewById(R.id.edtStatusCaption)
        imgStatusImage = findViewById(R.id.imgStatusPhoto)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        btnDone = findViewById(R.id.btnSendPhoto)
            getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    // You have the selected image URI, now upload it to storage and Firestore
                    selectedImageUri = uri
                    imgStatusImage.setImageURI(selectedImageUri)

                }
            }
            requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    // Permission granted, proceed with selecting and uploading the image
                    openGallery()
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                    checkAndRequestPermission()
                    Toast.makeText(this@SetStatus, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        // Set a click listener for the Select Photo button
        btnSelectPhoto.setOnClickListener {
            // Implement image selection logic here using an image picker library
            // After selecting an image, update the imgStatusImage ImageView
            // For example, you can use an image picker library like this:
            checkAndRequestPermission()
        }

        // Set a click listener for the Done button
            btnDone.setOnClickListener {
                val message = edtStatusText.text.toString() // Get the caption

                if (selectedImageUri != null && !message.isNullOrEmpty()) {
                    // Create a Status object
                    uploadStatusPictures(selectedImageUri)
                } else {
                    // Handle the case when either the image or message is empty
                    Toast.makeText(this@SetStatus, "Please select an image and enter a message.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    private fun openGallery() {
        getContent.launch("image/*")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, proceed with opening the gallery
            openGallery()
        } else {
            // Permission is not granted, request it
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
    }
    private fun uploadStatusPictures(selectedImageUri: Uri) {
        // Example: Upload the image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val mAuth = FirebaseAuth.getInstance()
        val imageStoragePath = "statusPictures/${mAuth.currentUser?.uid}_${System.currentTimeMillis()}.jpg"
        val imgReference = storageRef.child(imageStoragePath)

        val uploadTask = imgReference.putFile(selectedImageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get the download URL
            imgReference.downloadUrl.addOnSuccessListener { downloadUri ->
                // Now, 'downloadUri' contains the URL of the uploaded image

                // Continue with creating and storing the Status object
                val message = edtStatusText.text.toString() // Get the caption

                if (!message.isNullOrEmpty()) {
                    // Create a Status object with the download URL
                    val status = Status(downloadUri.toString(), message, Timestamp.now().seconds)

                    // Reference to the "statuses" collection
                    val statusesCollection = firestore.collection("status")

                    // Add the status to Firestore
                    statusesCollection.add(status)
                        .addOnSuccessListener { documentReference ->
                            // Status added to Firestore successfully
                            Toast.makeText(this@SetStatus, "Status added to Firestore", Toast.LENGTH_SHORT).show()

                            // You can finish this activity or navigate to another screen
                            startActivityForResult(Intent(this@SetStatus, StatusList::class.java), 100)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            // Handle Firestore upload failure
                            Toast.makeText(this@SetStatus, "${e.message}", Toast.LENGTH_SHORT).show()
                            Log.i("errorUploading", e.message.toString())
                        }
                } else {
                    // Handle the case when the message is empty
                    Toast.makeText(this@SetStatus, "Please enter a message.", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            // Handle image upload failure
            Toast.makeText(
                this@SetStatus,
                "Image upload failed: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
