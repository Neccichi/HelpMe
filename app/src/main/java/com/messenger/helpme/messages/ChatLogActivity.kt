package com.messenger.helpme.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.messenger.helpme.R
import com.messenger.helpme.models.Message
import com.messenger.helpme.models.User

class ChatLogActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User // variable to hold the User object for the selected user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Loading..." // или любая другая заглушка, пока не будет получен объект User

        // Отримуємо Id юзера
        val userId = intent.getStringExtra("USER_KEY")

        // Отримаэмо дані користувача з Firebase за допомогою ID
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)!!
                supportActionBar?.title = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // обработка ошибок
                Toast.makeText(this@ChatLogActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView = findViewById(R.id.recyclerview_chat_log)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // получаем список сообщений из Firebase
        val messagesRef = FirebaseDatabase.getInstance().getReference("/user-messages/$userId")
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                dataSnapshot.children.forEach { messageSnapshot ->
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let { messages.add(it) }
                }
                recyclerView.adapter = ChatAdapter(messages)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // обработка ошибок
                Toast.makeText(this@ChatLogActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

