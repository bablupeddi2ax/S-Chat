package com.example.simplechat.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.simplechat.R
import com.example.simplechat.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SnapActivity : AppCompatActivity() {
    private lateinit var edtCaption: EditText
    private lateinit var imgSelect: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var imageUri: Uri
    private lateinit var btnSendPhoto: Button
    private lateinit var receiverUid: String
    private lateinit var senderUid: String
    private lateinit var dbRef: DatabaseReference
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiverUid = intent.getStringExtra("receiverId").toString()
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        dbRef = FirebaseDatabase.getInstance().reference
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                uploadImage(imageUri)
                imgSelect.setImageURI(imageUri)
            }

        }
        setContentView(R.layout.activity_snap)
        val requestPermissionLauncher: ActivityResultLauncher<String> =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    // Permission granted, proceed with opening the gallery
                    openGallery(getContent)
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                    // You can inform the user about the need for the permission and ask again
                }
            }
        openGallery(getContent)
        imgSelect = findViewById(R.id.imgPickPhoto)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        edtCaption = findViewById(R.id.edtCaption)
        btnSendPhoto = findViewById(R.id.btnSendPhoto)

        btnSelectPhoto.setOnClickListener {
            btnSelectPhoto.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, proceed with opening the gallery
                    openGallery(getContent)
                } else {
                    // Permission is not granted, request it
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }


        }
        btnSendPhoto.setOnClickListener {
            val intent = Intent()
            intent.putExtra("newMessage", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    private fun uploadImage(imageUri: Uri?) {
        if (imageUri == null) {
            // Handle the case where imageUri is null (e.g., show an error message)
            Toast.makeText(this@SnapActivity, "Image not selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize storage reference
        val storageRef = FirebaseStorage.getInstance().reference
        // Generate unique filenames for sender and receiver images
        val senderFileName = "${System.currentTimeMillis()}_${senderUid}_${receiverUid}_sender.jpg"
        val receiverFileName = "${System.currentTimeMillis()}_${senderUid}_${receiverUid}_receiver.jpg"

        val sendReference = storageRef.child("images/$senderFileName")
        val receiverReference = storageRef.child("images/$receiverFileName")

        // Storage task for uploading sender image
        val sendTask = sendReference.putFile(imageUri)

        // Storage task for uploading receiver image
        val receiveTask = receiverReference.putFile(imageUri)

        // Handle sender image upload
        sendTask.continueWithTask { sendTask ->
            if (!sendTask.isSuccessful) {
                throw sendTask.exception ?: Exception("Unknown error occurred during sender image upload")
            }
            // Get sender image URL
            sendReference.downloadUrl
        }.continueWithTask { sendDownloadTask ->
            if (!sendDownloadTask.isSuccessful) {
                throw sendDownloadTask.exception ?: Exception("Unknown error occurred during sender image URL retrieval")
            }
            val senderImageUrl = sendDownloadTask.result.toString()

            // Handle receiver image upload
            receiveTask.continueWithTask { receiveTask ->
                if (!receiveTask.isSuccessful) {
                    throw receiveTask.exception ?: Exception("Unknown error occurred during receiver image upload")
                }
                // Get receiver image URL
                receiverReference.downloadUrl
            }.continueWith { receiveDownloadTask ->
                if (!receiveDownloadTask.isSuccessful) {
                    throw receiveDownloadTask.exception ?: Exception("Unknown error occurred during receiver image URL retrieval")
                }
                val receiverImageUrl = receiveDownloadTask.result.toString()

                // Check if both URLs are valid
                if (senderImageUrl.isNotEmpty() && receiverImageUrl.isNotEmpty()) {
                    // Create message objects
                    val senderMessageObject = Message(edtCaption.text.toString(), senderUid, senderImageUrl)
                    val receiverMessageObject = Message(edtCaption.text.toString(), receiverUid, receiverImageUrl)
                    var check = false
                    // Add message object in sender room then in receiver room
                    dbRef.child("chats").child(senderRoom).child("messages").push().setValue(senderMessageObject).addOnSuccessListener { check = true }.addOnFailureListener { check = false }
                    dbRef.child("chats").child(receiverRoom).child("messages").push().setValue(receiverMessageObject).addOnSuccessListener { check = true }.addOnFailureListener{check = false}
                    if(check){
                        val intent = Intent()
                        intent.putExtra("newMessageSent",true)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }

                    // Inform the user about successful upload
                    Toast.makeText(this@SnapActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the case where URLs are empty
                    Toast.makeText(this@SnapActivity, "URLs are empty", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            // Handle any failure during the process
            Toast.makeText(this@SnapActivity, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }




    private fun openGallery(getContent: ActivityResultLauncher<String>) {
        getContent.launch("image/*")
    }
}








































/*
  if (imageUri == null) {
            // Handle the case where imageUri is null (e.g., show an error message)
            Toast.makeText(this@SnapActivity, "Image not selected", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize storage reference
        val storageRef = FirebaseStorage.getInstance().reference
        // Storage paths for sender and receiver
        val sendReference = storageRef.child("image").child("$senderRoom.jpg")
        val receiverReference = storageRef.child("image").child("$receiverRoom.jpg")

        // Storage task for uploading images
        val sendTask = sendReference.putFile(imageUri)
       // val receiverTask = receiverReference.putFile(imageUri)

        sendTask.continueWithTask { sendTask ->
            if (!sendTask.isSuccessful) {
                throw sendTask.exception ?: Exception("Unknown error occurred during image upload")
            }
            // Get sender image URL
            sendReference.downloadUrl
        }.continueWithTask { sendDownloadTask ->
            if (!sendDownloadTask.isSuccessful) {
                throw sendDownloadTask.exception ?: Exception("Unknown error occurred during sender image URL retrieval")
            }
            val senderImageUrl = sendDownloadTask.result.toString()
            // Get receiver image URL
            receiverReference.downloadUrl
                .continueWith { receiverDownloadTask ->
                    if (!receiverDownloadTask.isSuccessful) {
                        throw receiverDownloadTask.exception ?: Exception("Unknown error occurred during receiver image URL retrieval")
                    }
                    val receiverImageUrl = receiverDownloadTask.result.toString()

                    // Check if both URLs are valid
                    if (senderImageUrl.isNotEmpty() && receiverImageUrl.isNotEmpty()) {
                        // Create message objects
                        val senderMessageObject = Message(edtCaption.text.toString(), senderUid, senderImageUrl)
                        val receiverMessageObject = Message(edtCaption.text.toString(), receiverUid, receiverImageUrl)
                            var check = false
                        // Add message object in sender room then in receiver room
                        dbRef.child("chats").child(senderRoom).child("messages").push().setValue(senderMessageObject).addOnSuccessListener { check = true }.addOnFailureListener { check = false }
                        dbRef.child("chats").child(receiverRoom).child("messages").push().setValue(receiverMessageObject).addOnSuccessListener { check = true }.addOnFailureListener{check = false}
                        if(check){
                            val intent = Intent()
                            intent.putExtra("newMessageSent",true)
                            setResult(Activity.RESULT_OK,intent)
                            finish()
                        }

                        // Inform the user about successful upload
                        Toast.makeText(this@SnapActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle the case where URLs are empty
                        Toast.makeText(this@SnapActivity, "URLs are empty", Toast.LENGTH_SHORT).show()
                    }
                }
        }.addOnFailureListener { exception ->
            // Handle any failure during the process
            Toast.makeText(this@SnapActivity, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
 */