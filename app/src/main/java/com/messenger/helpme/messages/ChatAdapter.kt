package com.messenger.helpme.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.messenger.helpme.R
import com.messenger.helpme.models.Message
import com.squareup.picasso.Picasso
import com.messenger.helpme.models.User

class ChatAdapter(
    private val messages: List<Message>,
    private val currentUser: String,
    private val users: List<User>

) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textView)
        private val messagePhoto: ImageView = itemView.findViewById(R.id.imageView)


        fun bind(message: Message, user: User) {
            messageText.text = message.text
            val photoUrl = if (itemViewType == 0) {
                // chat_to_row
                "https://amiel.club/uploads/posts/2022-03/1647661051_6-amiel-club-p-kartinki-manga-naruto-6.png"
            } else {
                // chat_from_row
                "https://i.pinimg.com/736x/49/b0/50/49b0501c70fde974bfeb85180561b8e9.jpg"
            }
            Picasso.get().load(photoUrl).into(messagePhoto)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = if (viewType == 0) {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_to_row, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_from_row, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages.getOrNull(position)?.fromId == currentUser) {
            0
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val user = users.find { it.uid == if (message.isSentByCurrentUser) currentUser else message.fromId } ?: User()
        holder.bind(message, user)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
