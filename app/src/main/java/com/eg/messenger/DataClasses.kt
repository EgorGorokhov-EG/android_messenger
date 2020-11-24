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
    var userId: String? = "",
    var userName: String? = "",
    var email: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "email" to email
        )
    }
}

data class Dialog(
    var user1: String? = "",
    var user2: String? = "",
    var lastMessage: String? = "",
    var timeLastMessageSent: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user1" to user1,
            "user2" to user2,
            "lastMessage" to lastMessage,
            "timeLastMessageSent" to timeLastMessageSent
        )
    }
}