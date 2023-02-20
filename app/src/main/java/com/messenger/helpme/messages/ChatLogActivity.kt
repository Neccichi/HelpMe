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
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        val photoAddButton = findViewById<Button>(R.id.photo_add_btn)
        photoAddButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        supportActionBar?.title = "Loading..."

        val userId = intent.getStringExtra("USER_KEY") ?: return

        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java) ?: return
                supportActionBar?.title = user.username

                messages = mutableListOf()
                val currentUser = FirebaseAuth.getInstance().currentUser?.let { User(it.uid, it.displayName ?: "", it.photoUrl?.toString() ?: "") }
                adapter = ChatAdapter(messages)

                recyclerView = findViewById(R.id.recyclerview_chat_log)
                recyclerView.layoutManager = LinearLayoutManager(this@ChatLogActivity)
                recyclerView.adapter = adapter

                //val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages/${FirebaseAuth.getInstance().uid}/$userId")
                val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages").child(getChatId(FirebaseAuth.getInstance().uid!!, userId))

                //val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages/${FirebaseAuth.getInstance().uid}-$userId")
                //val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages").child(FirebaseAuth.getInstance().uid!!).child(userId)
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





            //val fromId = FirebaseAuth.getInstance().uid ?: return@setOnClickListener
            // val toId = user.uid
            //val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
            //val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId-$toId").push()


            //val message = Message(ref.key ?: return@setOnClickListener, text, fromId!!, toId!!, System.currentTimeMillis() / 1000, true)

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            val ref = FirebaseStorage.getInstance().getReference("/images/${UUID.randomUUID()}")

            ref.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener { uri ->
                        Log.d(TAG, "File location: $uri")

                        val message = Message(
                            "",
                            "",
                            FirebaseAuth.getInstance().uid!!,
                            user.uid,
                            System.currentTimeMillis() / 1000,
                            true,
                            getChatId(FirebaseAuth.getInstance().uid!!, user.uid),
                            uri.toString()
                        )

                        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${message.chatId}").push()
                        ref.setValue(message)
                            .addOnSuccessListener {
                                Log.d(TAG, "Saved chat message with image: ${ref.key}")
                            }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to upload image to storage: ${it.message}")
                }
        }
    }

}