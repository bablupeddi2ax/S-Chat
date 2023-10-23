package com.example.simplechat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.adapters.StatusAdapter
import com.example.simplechat.models.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class StatusList : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth:FirebaseAuth
    private lateinit var statusList:ArrayList<Status>
    private lateinit var statusRecyclerView: RecyclerView
    private lateinit var mStatusAdapter:StatusAdapter
    private lateinit var fab:FloatingActionButton
    private lateinit var  statusRef:CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_list)
        
        firestore = Firebase.firestore
        mAuth = FirebaseAuth.getInstance()
        val currentUserId = mAuth.currentUser?.uid
        fab = findViewById(R.id.btnAddStatus)
        fab.setOnClickListener{
            val intent = Intent(this@StatusList,SetStatus::class.java)
            intent.putExtra("currentUserId",mAuth.currentUser?.uid.toString())
            startActivityForResult(intent,100)

        }
        statusRecyclerView = findViewById(R.id.statusRecyclerView)
        statusRecyclerView.layoutManager = LinearLayoutManager(this)
        statusList = ArrayList();
        mStatusAdapter = StatusAdapter(this@StatusList,statusList)
        statusRecyclerView.adapter = mStatusAdapter

        statusRef = firestore.collection("status");
        statusRef.addSnapshotListener(EventListener { snapshots, e->
            if(e!=null){
                return@EventListener
            }
            val statuses = snapshots?.documents?.map{
                it.toObject(Status::class.java)!!
            }
            if (statuses != null) {
                statusList.addAll(statuses)
                mStatusAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val statusRef = Firebase.firestore.collection("status")
        statusRef.addSnapshotListener(EventListener<QuerySnapshot>{snapshots,e->
            if(e!=null){
                return@EventListener
            }
            statusList.clear()
            val statuses = snapshots?.documents?.map{
                it.toObject(Status::class.java)!!
            }
            if (statuses != null) {
                statusList.addAll(statuses)
                mStatusAdapter.notifyDataSetChanged()
            }
        })
    }
}