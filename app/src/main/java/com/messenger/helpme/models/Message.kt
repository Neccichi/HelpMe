package com.messenger.helpme.models

data class Message(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val time: Long,
    val isSentByCurrentUser: Boolean // новое свойство для указания, отправлено ли сообщение текущим пользователем
) {
    constructor() : this("", "", "", "", -1, false) // Required default constructor for Firebase
}