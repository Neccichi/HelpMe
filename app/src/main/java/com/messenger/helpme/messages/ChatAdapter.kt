package com.messenger.helpme.messages

import android.content.Intent
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

class ChatAdapter(
    private val messages: List<Message>,
    private val currentUser: String,
    private val users: List<User>,
    private val userKeyAvatar: String?
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    companion object {
        const val USER_KEY = "USER_KEY"

    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textView)
        private val messageAvatar: ImageView = itemView.findViewById(R.id.imageView)
        private val messagePhoto: ImageView = itemView.findViewById(R.id.imageView2)

        fun bind(message: Message, user: User) {
            if (!message.photoUrl.isNullOrEmpty()) {
                messagePhoto.visibility = View.VISIBLE
                messageText.visibility = View.GONE
                Picasso.get().load(message.photoUrl).into(messagePhoto)
            } else {
                messagePhoto.visibility = View.GONE
                messageText.visibility = View.VISIBLE
                messageText.text = message.text
            }

            val PleaseWork = userKeyAvatar

            val photoUrl = if (itemViewType == 1) {
                // chat_to_row (messages sent by other users)
                PleaseWork ?: "https://i.pinimg.com/736x/49/b0/50/49b0501c70fde974bfeb85180561b8e9.jpg"
            } else {
                // chat_from_row (messages sent by current user)
                LatestMessagesActivity.currentUser?.profileImageUrl
            }

            Picasso.get().load(photoUrl).into(messageAvatar)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = if (viewType == 1) {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_from_row, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_to_row, parent, false)
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
        val intent = Intent(holder.itemView.context, ChatLogActivity::class.java)
        intent.putExtra(USER_KEY, user.uid)
        holder.bind(message, user)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
