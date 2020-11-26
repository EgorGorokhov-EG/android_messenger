/*
package com.eg.messenger

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class Utils {
    //TODO: Make this shit works


    // Function to retrieve information about current user from Database
    fun getUserNameFromDB(userId: String?, database: DatabaseReference): String? {
        var userName: String? = ""

        class MyValueEventListener : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.value
                //println(user.toString())
            }
        }

        val retrieveUserEventListener = MyValueEventListener()
        val userQuery = database.child("users").orderByKey().equalTo(userId)
        userQuery.addListenerForSingleValueEvent(retrieveUserEventListener)
        return userName
    }

    fun getUserIdListener(authId: String?, database: DatabaseReference): String? {

        val userIdQuery = database.child("users").orderByChild("authId").equalTo(authId)
        userIdQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val snapValue = snapshot.value
                userId = (snapValue as HashMap<*,*>).keys.toList()[0] as String?
                println(userId + " in onDataChange")
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        println(userId + " before return")
        return userId
    }

    fun getChatIdFromDB(userId: String?, database: DatabaseReference) {
        var currentChatId: String? = ""

        class RetrieveChatEventListener : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //currentChatId = snapshot.children
                println(snapshot.toString())
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        val listener = RetrieveChatEventListener()
        val chatQuery = database.child("chats").orderByChild("lastMessage").equalTo(userId)
        chatQuery.addListenerForSingleValueEvent(listener)
    }
}*/
