package com.messenger.helpme.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.messenger.helpme.R


class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Chat log"
    }
}