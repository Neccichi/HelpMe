package com.messenger.helpme.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.messenger.helpme.R
import com.squareup.picasso.Picasso
import com.messenger.helpme.models.User
import de.hdodenhof.circleimageview.CircleImageView



class NewMessageActivity : AppCompatActivity() {
    companion object {
        const val USER_KEY = "USER_KEY"
    }
    private lateinit var recyclerviewNewMessage: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.apply {
            title = "Select User"
            setDisplayHomeAsUpEnabled(true)
        }

        recyclerviewNewMessage = findViewById(R.id.recyclerview_newmessage)
        recyclerviewNewMessage.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter()
        recyclerviewNewMessage.adapter = adapter

        fetchUsers()

    }
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.addUser(user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}

class UserAdapter : RecyclerView.Adapter<UserViewHolder>() {
    private val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row_new_message, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }
}

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageViewNewMessage = itemView.findViewById<CircleImageView>(R.id.imageview_new_message)
    private val usernameTextViewNewMessage = itemView.findViewById<TextView>(R.id.username_textview_new_message)

    fun bind(user: User) {
        usernameTextViewNewMessage.text = user.username
        Picasso.get().load(user.profileImageUrl).into(imageViewNewMessage)


        itemView.setOnClickListener {
            val intent = Intent(itemView.context, ChatLogActivity::class.java)
            intent.putExtra("USER_KEY", user.uid)
            itemView.context.startActivity(intent)
            (itemView.context as NewMessageActivity).finish()
        }
    }
}



