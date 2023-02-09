package com.messenger.helpme.models

data class Message(
    val text: String,
    val fromId: String,
    val timestamp: Long
)
