package com.eg.messenger

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
    var id: String? = "",
    var authId: String? = "",
    var userName: String? = "",
    var email: String? = "",
    var chats: List<String?> = listOf()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            id as String to true,
            "authId" to authId,
            "userName" to userName,
            "email" to email,
            "chats" to chats
        )
    }
}

data class Chat(
    var users: MutableMap<String?, Boolean> = mutableMapOf(),
    var lastMessage: String? = "",
    var timeLastMessageSent: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "users" to users,
            "lastMessage" to lastMessage,
            "timeLastMessageSent" to timeLastMessageSent
        )
    }
}