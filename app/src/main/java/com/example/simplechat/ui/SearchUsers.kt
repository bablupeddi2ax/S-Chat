package com.example.simplechat.ui

import UserAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Features:
 *   User Search Functionality:
 *   - Allows users to search for other users using text input.
 *   - Retrieves a list of users from Firebase Realtime Database and filters based on the search query.
 *   - Displays search results in a RecyclerView using a custom adapter.
 *
 *   Firebase Integration:
 *   - Initializes Firebase components, including Firebase Authentication and Realtime Database.
 *   - Retrieves user data from the Realtime Database.
 *
 *   Realtime Filtering:
 *   - Uses a TextWatcher to listen for changes in the search input field and dynamically filters the user list.
 *
 *   Code Organization:
 *   - Organizes code into functions for clarity and readability.
 *   - Utilizes a custom adapter (UserAdapter) to display user search results.
 **/
class SearchUsers : AppCompatActivity() {
    private lateinit var mRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var edtSearch: EditText
    private lateinit var btnSearchUsers: ImageButton

    // the plan is to get all users into the loist and then filter them with the neterded text and then updte the lsiteview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

       
        // initialize firebase
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference // same as getReference("")
        // initialize variables
        userList = ArrayList()
        adapter = UserAdapter(this@SearchUsers, userList)
        userRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        edtSearch = findViewById(R.id.edtSearch)
        btnSearchUsers = findViewById(R.id.btnPersonSearch)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchQuery = p0.toString()
                filterList(searchQuery)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        mRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val userObject = postSnapshot.getValue(User::class.java)
                    if (userObject?.uid != mAuth.currentUser?.uid) {
                        userList.add(userObject!!)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchUsers,"Some problem occurred",Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun filterList(searchQuery: String) {
        val filteredList = userList.filter {
            it.name!!.contains(searchQuery, ignoreCase = true)
        }
        adapter.updateList(filteredList)

    }


}