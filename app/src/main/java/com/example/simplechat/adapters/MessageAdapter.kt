package com.example.simplechat.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.simplechat.R
import com.example.simplechat.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>):RecyclerView.Adapter<ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2
    private val ITEM_IMAGE = 3 //sent image
    private val ITEM_IMAGE_RECEIVED = 4//received image
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType==1){
            val view:View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewHolder(view)
        }
        else if(viewType==2){
            val view:View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }else if(viewType==3 ) {
            // Create a view holder for image messages (you need to define the layout for image messages)
            val view: View = LayoutInflater.from(context).inflate(R.layout.image_message_sent, parent, false)
            view.setOnClickListener{
                Toast.makeText(context,"clicked on image sent " ,Toast.LENGTH_SHORT).show()
            }
            return SentImageViewHolder(view)
        }
        else {
            // Handle other view types or return a default view holder
            val view: View = LayoutInflater.from(context).inflate(R.layout.image_message_receive, parent, false)
            view.setOnClickListener{
                Toast.makeText(context,"clicked on image recibved ",Toast.LENGTH_SHORT).show()
            }
            return ReceiveImageViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when (holder) {
            is SentViewHolder -> {
                holder.sentMessage.text = currentMessage.message.toString()
                holder.sentMessage.gravity = Gravity.END
            }
            is ReceiveViewHolder -> {
                holder.receiveMessage.text = currentMessage.message.toString()
                holder.receiveMessage.gravity = Gravity.START
            }
            is SentImageViewHolder-> {
                Picasso.get().load(currentMessage.imageUrl).into(holder.sentImageView)
                holder.sentCaptionView.text = currentMessage.message
                holder.sentImageView.foregroundGravity = Gravity.END

            }
            is ReceiveImageViewHolder->{
                Picasso.get().load(currentMessage.imageUrl).into(holder.receiveImageView)
                holder.receiveCaption.text = currentMessage.message
                holder.receiveImageView.foregroundGravity = Gravity.START
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return when {
            FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderUid && currentMessage.imageUrl.isNullOrEmpty() -> ITEM_SENT
            FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderUid&& !currentMessage.imageUrl.isNullOrEmpty() -> ITEM_IMAGE
            FirebaseAuth.getInstance().currentUser?.uid != currentMessage.senderUid&& currentMessage.imageUrl.isNullOrEmpty() -> ITEM_RECEIVE
            else -> ITEM_IMAGE_RECEIVED // Handle other cases as needed
        }
    }

    class SentViewHolder(itemView: View):ViewHolder(itemView)
    {
        val sentMessage: TextView = itemView.findViewById(R.id.txtSentMessage)
    }
    class ReceiveViewHolder(itemView: View):ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.txtreceiveMessage)
    }
    class SentImageViewHolder(itemView: View): ViewHolder(itemView) {
        val sentImageView: ImageView = itemView.findViewById(R.id.imgSent)
        val sentCaptionView:TextView  = itemView.findViewById(R.id.captionSent)
    }
    class ReceiveImageViewHolder(itemView: View):ViewHolder(itemView){
        val receiveImageView:ImageView=itemView.findViewById(R.id.imgReceive)
        val receiveCaption : TextView = itemView.findViewById(R.id.captionReceive)
    }
}












//package com.example.simplechat.adapters
//
//import android.content.Context
//import android.media.Image
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.RecyclerView.ViewHolder
//import com.example.simplechat.R
//import com.example.simplechat.models.Message
//import com.google.firebase.auth.FirebaseAuth
//import com.squareup.picasso.Picasso
//
//class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>):RecyclerView.Adapter<ViewHolder>() {
//
//    private val ITEM_RECEIVE = 1
//    private val ITEM_SENT = 2
//    private val ITEM_IMAGE = 3 //sent image
//    private val ITEM_IMAGE_RECEIVED = 4//received image
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        if(viewType==1){
//            val view:View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
//            return ReceiveViewHolder(view)
//        }
//        else if(viewType==2){
//            val view:View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
//            return SentViewHolder(view)
//        }else if(viewType==3 ) {
//            // Create a view holder for image messages (you need to define the layout for image messages)
//            val view: View = LayoutInflater.from(context).inflate(R.layout.image_message_sent, parent, false)
//            view.setOnClickListener{
//                Toast.makeText(context,"clicked on image sent " ,Toast.LENGTH_SHORT).show()
//            }
//            return SentImageViewHolder(view)
//        }
//        else {
//            // Handle other view types or return a default view holder
//            val view: View = LayoutInflater.from(context).inflate(R.layout.image_message_receive, parent, false)
//            view.setOnClickListener{
//                Toast.makeText(context,"clicked on image recibved ",Toast.LENGTH_SHORT).show()
//            }
//            return ReceiveImageViewHolder(view)
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//      return messageList.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentMessage = messageList[position]
//        when (holder) {
//            is SentViewHolder -> {
//                holder.sentMessage.text = currentMessage.message.toString()
//                holder.sentMessage.gravity = Gravity.END
//
//            }
//            is ReceiveViewHolder -> {
//                holder.receiveMessage.text = currentMessage.message.toString()
//                holder.receiveMessage.gravity = Gravity.START
//            }
//            is SentImageViewHolder-> {
//                Picasso.get().load(currentMessage.imageUrl).into(holder.sentImageView)
//                holder.sentCaptionView.text = currentMessage.message
//                holder.sentImageView.foregroundGravity = Gravity.END
//
//            }
//            is ReceiveImageViewHolder->{
//                Picasso.get().load(currentMessage.imageUrl).into(holder.receiveImageView)
//                holder.receiveCaption.text = currentMessage.message
//                holder.receiveImageView.foregroundGravity = Gravity.START
//            }
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val currentMessage = messageList[position]
//        return when {
//            FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderUid && currentMessage.imageUrl.isNullOrEmpty() -> ITEM_SENT
//            FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderUid&& !currentMessage.imageUrl.isNullOrEmpty() -> ITEM_IMAGE
//            FirebaseAuth.getInstance().currentUser?.uid != currentMessage.senderUid&& currentMessage.imageUrl.isNullOrEmpty() -> ITEM_RECEIVE
//            else -> ITEM_IMAGE_RECEIVED // Handle other cases as needed
//        }
//    }
//
//    class SentViewHolder(itemView: View):ViewHolder(itemView)
//    {
//        val sentMessage: TextView = itemView.findViewById(R.id.txtSentMessage)
//    }
//    class ReceiveViewHolder(itemView: View):ViewHolder(itemView){
//        val receiveMessage: TextView = itemView.findViewById(R.id.txtreceiveMessage)
//    }
//    class SentImageViewHolder(itemView: View): ViewHolder(itemView) {
//         val sentImageView: ImageView = itemView.findViewById(R.id.imgSent)
//        val sentCaptionView:TextView  = itemView.findViewById(R.id.captionSent)
//
//    }
//    class ReceiveImageViewHolder(itemView: View):ViewHolder(itemView){
//        val receiveImageView:ImageView=itemView.findViewById(R.id.imgReceive)
//        val receiveCaption : TextView = itemView.findViewById(R.id.captionReceive)
//    }
//}