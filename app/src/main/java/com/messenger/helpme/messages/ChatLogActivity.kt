package com.messenger.helpme.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.messenger.helpme.R
import com.messenger.helpme.RegisterActivity.Companion.TAG
import com.messenger.helpme.models.Message
import com.messenger.helpme.models.User

class ChatLogActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    private lateinit var messages: MutableList<Message>
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Loading..."

        val userId = intent.getStringExtra("USER_KEY")

        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)!!
                supportActionBar?.title = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })

        recyclerView = findViewById(R.id.recyclerview_chat_log)
        recyclerView.layoutManager = LinearLayoutManager(this)

        messages = mutableListOf()
        adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter

        val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages/${FirebaseAuth.getInstance().uid}/$userId")
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(Message::class.java)
                messages.add(message!!)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }
}


