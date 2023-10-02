package com.example.simplechat.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.utils.Utils
import com.example.simplechat.adapters.MessageAdapter
import com.example.simplechat.models.Message
import com.example.simplechat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * Features:
 * - Real-time Chat:
 *   Enables users to have real-time chat conversations with other users.
 *   Messages are stored in Firebase Realtime Database.

 * - Message List Display:
 *   Displays a list of chat messages in a RecyclerView.
 *   Messages are retrieved from the database and updated in real-time.

 * - Message Sending:
 *   Allows users to send messages to the selected recipient.
 *   Messages are sent and received via Firebase Realtime Database.

 * - Dynamic Chat Rooms:
 *   Creates dynamic chat rooms for each chat pair by combining user UIDs.
 *   Ensures private conversations between users.

 * - UI Interaction:
 *   Provides an interface for users to type and send messages.
 *   Automatically scrolls to the latest message for a better chat experience.

 * - Code Organization:
 *   Utilizes a custom adapter (MessageAdapter) for displaying chat messages.
 */

class ChatActivity : AppCompatActivity() {
    // Define variables
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mRef: DatabaseReference
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private lateinit var utils: Utils
    private lateinit var imageSendButton:ImageButton
    private lateinit var receiverUid :String
    private lateinit var senderUid:String
    val REQUEST_CODE_SNAP_ACTIVITY = 100
    private fun sendNotification(message: String) {
        // current username, message, currentUserId, other user token
        val curUid = FirebaseAuth.getInstance().currentUser?.uid
        var currUserName = ""
        var fcmToken = ""
        val receiverUid = intent.getStringExtra("uid").toString()

        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val query = usersRef.orderByKey().equalTo(receiverUid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(receiverSnapshot: DataSnapshot) {
                if (receiverSnapshot.exists()) {
                    for (userSnapShot in receiverSnapshot.children) {
                        val receiverObject = userSnapShot.getValue(User::class.java)
                        fcmToken = receiverObject?.fcmToken.toString()

                        // Fetch current user details
                        val currentUserQuery = usersRef.orderByKey().equalTo(curUid)
                        currentUserQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(currentUserSnapshot: DataSnapshot) {
                                if (currentUserSnapshot.exists()) {
                                    for (currentUserSnapShot in currentUserSnapshot.children) {
                                        val userObj = currentUserSnapShot.getValue(User::class.java)
                                        currUserName = userObj?.name.toString()

                                        // Create and send the notification here
                                        try {
                                            val jsonObject = JSONObject()
                                            val notificationObject = JSONObject()
                                            notificationObject.put("title", currUserName)
                                            notificationObject.put("body", message)
                                            val dataObject = JSONObject()
                                            dataObject.put("userId", userObj?.uid.toString())
                                            jsonObject.put("notification", notificationObject)
                                            jsonObject.put("data", dataObject)
                                            jsonObject.put("to", fcmToken)

                                            // Send the notification using Firebase Cloud Messaging
                                            // You should implement this part to send the notification
                                            // using an HTTP POST request to the FCM server.
                                            callApi(jsonObject)
                                        } catch (e: Exception) {
                                            // Handle any exceptions
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(currentUserError: DatabaseError) {
                                // Handle the error
                            }
                        })
                    }
                }
            }

            override fun onCancelled(receiverError: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun callApi(jsonObject: JSONObject) {

        val JSON = "application/json".toMediaType()

        val client = OkHttpClient()


            val body = jsonObject.toString().toRequestBody(JSON)
            val url = "https://fcm.googleapis.com/fcm/send"
            val request = Request.Builder()
                .url(url)
                .post(body)
                .header(name = "Authorization","Bearer AAAAsb871t4:APA91bHoXymE3ZfFWqbuYJW2SQlH6Ll-wMyJ0c0Qch6CrIcMhirU0cq3_4J0TThwQ4kgWMyjaYM9QHMcHCF0D2ukLUo2XGr-49gvb68FNHQxs_LYWYjXpBlHlssqaoUfUgJXSAHnvlDm")
                .build()
            client.newCall(request).enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {

                }
            })


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Retrieve user details and initialize Firebase references
         val name = intent.getStringExtra("name")
         receiverUid = intent.getStringExtra("uid").toString()
         senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mRef = FirebaseDatabase.getInstance().reference

        // initialize senderRoom and receiverRoom be the combination of receiverUid and senderUid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        utils = Utils()
        // add receiver name as title of supportActionBar
        supportActionBar?.title = name

        // initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        sendButton = findViewById(R.id.sendBtn)
        messageBox = findViewById(R.id.messageBox)
        // TODO added photo button test this properly to avoid errors
        imageSendButton = findViewById(R.id.btnSendPhoto)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        // Configure the RecyclerView for displaying messages
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // Retrieve and display chat messages in real-time
        mRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // avoid duplication by clearing messages that are already there in the list
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    //notify data set changed
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    utils.showToast(error.message, this@ChatActivity)
                }
            })


        // Send messages when the send button is clicked
        sendButton.setOnClickListener {
            utils.showToast("check",this@ChatActivity, )
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)
            // add message object in sender room then in receiver room
            mRef
                .child("chats")
                .child(senderRoom!!)
                .child("messages")
                .push().setValue(messageObject).addOnSuccessListener {
                    mRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            sendNotification(message)
                        }
                }
            //scroll to show the latest message
            chatRecyclerView.scrollToPosition(messageList.size-1 )
            //clear message box
            messageBox.setText("")
        }
        imageSendButton.setOnClickListener{
            utils.showToast("Inside imageSendButton",this@ChatActivity)
            val intent = Intent(this@ChatActivity,SnapActivity::class.java)
            utils.showToast(receiverUid,this)
            utils.showToast(senderUid,this)
            intent.putExtra("receiverId",receiverUid)
            intent.putExtra("senderId",senderUid)
            startActivityForResult(intent,100)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SNAP_ACTIVITY && resultCode == Activity.RESULT_OK) {
            // Check if a new message has been sent from SnapActivity
            val newMessageSent = data?.getBooleanExtra("newMessageSent", false) ?: false
            if (newMessageSent) {
                mRef.child("chats").child(senderRoom!!).child("messages")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // avoid duplication by clearing messages that are already there in the list
                            messageList.clear()
                            for (postSnapshot in snapshot.children) {
                                val message = postSnapshot.getValue(Message::class.java)
                                messageList.add(message!!)
                            }
                            //notify data set changed
                            messageAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            utils.showToast(error.message, this@ChatActivity)
                        }
                    })
            }
        }
    }
}