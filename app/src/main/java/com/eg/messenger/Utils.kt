package com.eg.messenger

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

// Function to retrieve information about current user from Database
fun getCurrentUserFromDB(userId: String?, database: DatabaseReference): String? {
    var currentUserName: String? = ""

    class MyValueEventListener(): ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            val user = snapshot.children.toList()[0].value as MutableMap<*,*>
            currentUserName = user["userName"] as String?
        }
    }

    val retrieveUserEventListener = MyValueEventListener()
    val userQuery = database.child("users").orderByChild("userId").equalTo(userId)
    userQuery.addListenerForSingleValueEvent(retrieveUserEventListener)
    return currentUserName
}