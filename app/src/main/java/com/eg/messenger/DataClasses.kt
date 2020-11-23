package com.eg.messenger

data class Message(
    var userId: String? = "",
    var userName: String? = "",
    var createdAt: Long? = 0,
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
    var userId: String?,
    var userName: String?,
    var email: String?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "email" to email
        )
    }
}