package com.example.simplechat.ui

import UserAdapter
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.utils.Utils
import com.example.simplechat.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import android.Manifest

class MainActivity : AppCompatActivity(), ActivityResultCallback<Boolean> {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var utils: Utils
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageUri: Uri
    private lateinit var imgDialog: ImageView
    private lateinit var selectImageDialogButton: Button
    private lateinit var getContent: ActivityResultLauncher<String>

    val REQUEST_CODE_GALLERY_PERMISSION = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val notificationData = intent.extras
        if (notificationData != null) {
            // Handle the data from the notification here
            mAuth = FirebaseAuth.getInstance()
            val mdbRef = FirebaseDatabase.getInstance().getReference("users")
            val userId = intent.extras?.getString("userId")
            mdbRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(userSnapShot in snapshot.children){
                        if(userSnapShot.key==userId){
                            val userObject = userSnapShot.getValue(User::class.java)
                            // Navigate to the relevant part of your app based on chatId and messageId
                            // For example, you can open a specific chat or message thread
                            val intent = Intent(this@MainActivity, ChatActivity::class.java)
                            intent.putExtra("name", userObject?.name)
                            intent.putExtra("uid", userObject?.uid)
                            Toast.makeText(this@MainActivity,userId,Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Something happend$error",Toast.LENGTH_SHORT).show()
                }
            })

        }

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("users")
        firestore = Firebase.firestore

        val currentUserUID = mAuth.currentUser?.uid!!

        // Initialize variables and recycler view
        userList = ArrayList()
        adapter = UserAdapter(this@MainActivity, userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        utils = Utils()
        checkNotificationPermission()
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                uploadImage(imageUri)
                createImageSelectionDialog()
                imgDialog.setImageURI(imageUri)
            }
        }

        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapShot in snapshot.children) {
                    val currentUser = userSnapShot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
            }
        })

        val profilesCollection = firestore.collection("profiles")

        profilesCollection
            .document(currentUserUID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    checkGalleryPermission()
                    navigateToSetProfile()
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that may occur during the Firestore query
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToSetProfile() {
        val intent = Intent(this, SetProfileActivity::class.java)
        startActivity(intent)
    }





    private fun checkGalleryPermission() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        getContent.launch("image/*")

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_GALLERY_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch SetProfileActivity
                navigateToSetProfile()
            } else {
                // Permission denied, you can show a message to the user or handle it accordingly
                Toast.makeText(this, "Gallery access permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createImageSelectionDialog(): AlertDialog {
        val dialogView = layoutInflater.inflate(R.layout.profile_picture_dialog, null)
        imgDialog = dialogView.findViewById(R.id.selectImageDialogView)
        selectImageDialogButton = dialogView.findViewById(R.id.selectImageDialogButton)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("ok") { dialog, which ->
            // Handle OK button click
        }
        builder.setNegativeButton("I will do it later") { dialog, which ->
            // Handle "I will do it later" button click
        }

        selectImageDialogButton.setOnClickListener {
            openGallery(getContent)
        }

        return builder.create()
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        } else {
            // For devices prior to Android 8, show a toast to explain how to enable notifications
            Toast.makeText(this, "Please enable notifications for this app in system settings", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            finish()
            utils.moveTo(this@MainActivity, Login::class.java)
            return true
        }
        if (item.itemId == R.id.search) {
            utils.moveTo(this@MainActivity, SearchUsers::class.java)
            return true
        }
        if(item.itemId==R.id.profile){
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast

                Log.d("FCMTOKEN",token.toString())
                Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()
            })
            utils.moveTo(this@MainActivity,ProfileActivity::class.java)

            return true
        }
        return true
    }
    private fun isNotificationPermissionGranted(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun checkNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            // Notification permission is not granted, show a button to request it
            requestNotificationPermission()
        }
    }
    private fun uploadImage(imageUri: Uri?) {
        if (imageUri == null) {
            Toast.makeText(this@MainActivity, "Image not selected", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val imageStoragePath = "profilePictures/${mAuth.currentUser?.uid}.jpg"
        val imgReference = storageRef.child(imageStoragePath)

        val uploadTask: UploadTask = imgReference.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imgReference.downloadUrl.addOnSuccessListener { downloadUri ->
                val userUid = mAuth.currentUser?.uid
                if (userUid != null) {
                    val firestore = Firebase.firestore
                    val profilesCollection = firestore.collection("profiles")
                    val userDocument = profilesCollection.document(userUid)
                    val updateData = mapOf("profilePictureDownloadUri" to downloadUri.toString())

                    userDocument.update(updateData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@MainActivity,
                                "Image uploaded and URL updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@MainActivity,
                                "Firestore update failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this@MainActivity, "User UID is null", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this@MainActivity,
                "Image upload failed: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openGallery(getContent: ActivityResultLauncher<String>) {
        getContent.launch("image/*")
    }

    override fun onActivityResult(result: Boolean?) {
        // Handle the result here
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "Toast.makeText(this, \"Back button is disabled in this screen.\", Toast.LENGTH_SHORT).show()",
        "android.widget.Toast",
        "android.widget.Toast"
    )
    )
    override fun onBackPressed() {
        Toast.makeText(this, "Back button is disabled in this screen.", Toast.LENGTH_SHORT).show()
    }
}