package com.eg.messenger.data

data class Message(
    var userId: String? = "",
    var userName: String? = "",
    var createdAt: String? = "00:00",
    var messageBody: String? = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "createdAt" to createdAt,
            "messageBody" to messageBody
        )
    }
}

data class User(
    var userId: String? = "",
    var userName: String? = "",
    var email: String? = "",
    var chats: MutableList<String?> = mutableListOf()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            userId as String to true,
            "userName" to userName,
            "email" to email,
            "chats" to chats
        )
    }
}

data class Chat(
    var chatId: String? = "",
    var users: List<String?> = listOf(),
    var lastMessage: String? = "",
    var timeLastMessageSent: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            chatId as String to true,
            "users" to users,
            "lastMessage" to lastMessage,
            "timeLastMessageSent" to timeLastMessageSent
        )
    }
}