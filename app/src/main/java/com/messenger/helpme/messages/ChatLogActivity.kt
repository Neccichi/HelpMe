package com.messenger.helpme.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.messenger.helpme.R
import com.messenger.helpme.RegisterActivity.Companion.TAG
import com.messenger.helpme.models.Message
import com.messenger.helpme.models.User
import com.messenger.helpme.registerlogin.LoginActivity
import java.util.*

class ChatLogActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    private lateinit var messages: MutableList<Message>
    private lateinit var adapter: ChatAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        supportActionBar?.title = "Loading..."

        val userId = intent.getStringExtra("USER_KEY") ?: return

        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java) ?: return
                supportActionBar?.title = user.username

                messages = mutableListOf()
                adapter = ChatAdapter(messages)

                recyclerView = findViewById(R.id.recyclerview_chat_log)
                recyclerView.layoutManager = LinearLayoutManager(this@ChatLogActivity)
                recyclerView.adapter = adapter

                val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages").child(getChatId(FirebaseAuth.getInstance().uid!!, userId))
                messagesRef.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        val message = dataSnapshot.getValue(Message::class.java) ?: return
                        messages.add(message)
                        adapter.notifyItemInserted(messages.size - 1)
                        recyclerView.smoothScrollToPosition(messages.size - 1)
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

                    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException())
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })

        val sendButton = findViewById<Button>(R.id.send_button_chat_log)
        val editText = findViewById<EditText>(R.id.edittext_chat_log)

        sendButton.setOnClickListener {
            val text = editText.text.toString()

            if (text.isEmpty()) {
                return@setOnClickListener
            }

            val fromId = FirebaseAuth.getInstance().uid ?: return@setOnClickListener
            val toId = user.uid
            val chatId = getChatId(fromId, toId)
            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$chatId").push()

            val message = Message(ref.key ?: return@setOnClickListener, text, fromId, toId, System.currentTimeMillis() / 1000, true, chatId)
            ref.setValue(message)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${ref.key}")
                    editText.text.clear()
                }
        }
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) {
            "$uid1-$uid2"
        } else {
            "$uid2-$uid1"
        }
    }

}