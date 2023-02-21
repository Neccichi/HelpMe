package com.messenger.helpme.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.messenger.helpme.R
import com.messenger.helpme.models.Message
import com.squareup.picasso.Picasso
import com.messenger.helpme.models.User

class ChatAdapter(private val messages: List<Message>, private val currentUser: String) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textView)
        private val messagePhoto: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(message: Message, user: User) {
            messageText.text = message.text
            if (user.profileImageUrl.isNotEmpty()) {
                Picasso.get().load(user.profileImageUrl).into(messagePhoto)
            }
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
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val user = User() // Replace this with actual user object
        holder.bind(message, user)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
