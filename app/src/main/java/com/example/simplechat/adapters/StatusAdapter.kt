package com.example.simplechat.adapters

import android.app.Dialog
import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.models.Status
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.sql.Timestamp
import java.util.logging.Handler

class StatusAdapter(private val context: Context,private var sList:ArrayList<Status>): RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {


    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusImage = itemView.findViewById<ImageView>(R.id.statusImage)
        val statusMessage = itemView.findViewById<TextView>(R.id.statusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.status_layout, parent, false)
        return StatusViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sList.size;
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val currStatus = sList[position]

        // Set the status message
        holder.statusMessage.text = currStatus.getMessage()

        // Retrieve the image URL from the current Status object
        val imageUri = currStatus.getImageUri()

        // Load the image into the ImageView using Picasso
        if (!imageUri.isNullOrEmpty()) {
            Picasso.get().load(imageUri).into(holder.statusImage)
        } else {
            holder.statusImage.setImageResource(R.drawable.snapchat)
        }
        holder.statusImage.setOnClickListener {
            showImageDialog(context, imageUri)
        }
      //  removeStatusAfter24Hours(currStatus)
    }

    private fun showImageDialog(context: Context, imageUri: String?) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.image_dialog)

        val dialogImage = dialog.findViewById<ImageView>(R.id.dialogImage)

        if (!imageUri.isNullOrEmpty()) {
            // Load the image into the dialog's ImageView using Picasso
            Picasso.get().load(imageUri).into(dialogImage)
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Allow canceling the dialog by tapping outside of it
        dialog.setCanceledOnTouchOutside(true)

        // Show the dialog
        dialog.show()
    }
    // Define a function to remove a status after a specific time period (e.g., 24 hours)
    private fun removeStatusAfter24Hours(status: Status) {
        val looper = Looper.getMainLooper()
        val handler = android.os.Handler(looper)
        val delay = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

        handler.postDelayed({

             //removeStatusFromFirebase(status)

        }, delay.toLong())
    }

//    private fun removeStatusFromFirebase(status: Status) {
//        val fireStore = Firebase.firestore
//        val statusRef = fireStore.collection("status")
//            .addSnapshotListener{querySnapShot:QuerySnapshot?,exception:FirebaseException?->
//                run {
//                    for (snapshot in querySnapShot?.documents!!) {
//
//                    }
//                }
//            }
//
//        // Calculate the timestamp for 24 hours ago
//        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
//
//        statusRef
//            .whereEqualTo("userid", status.userId) // Replace with the correct field name
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot.documents) {
//                    val timestamp = document.getTimestamp("timestamp")
//                    if (timestamp != null && timestamp.toDate().time < cutoffTime) {
//                        // The status is older than 24 hours, delete it
//                        document.reference.delete()
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                // Handle any errors here
//            }
//    }



}