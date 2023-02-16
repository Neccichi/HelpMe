package com.messenger.helpme.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.messenger.helpme.R
import com.messenger.helpme.models.Message

class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textView)

        fun bind(message: Message) {
            messageText.text = message.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = if (viewType == 0) {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_from_row, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_to_row, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByCurrentUser) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}

