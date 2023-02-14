package com.messenger.helpme.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.messenger.helpme.R
import com.messenger.helpme.models.User

class ChatLogActivity : AppCompatActivity() {

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
            //rerere
            }
        })
    }
}

