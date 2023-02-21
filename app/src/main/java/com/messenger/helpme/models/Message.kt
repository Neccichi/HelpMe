package com.messenger.helpme.models

data class Message(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val time: Long,
    val isSentByCurrentUser: Boolean ,
    val chatId: String="",
    val profileImageInChat: String="    ",
    val photoUrl: String=""
) {
    constructor() : this("", "", "", "", -1, false)
}