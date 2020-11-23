package com.eg.messenger

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class DialogActivity : AppCompatActivity() {

    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private val currentUserId = auth.currentUser?.uid
    private var currentUserName: String? = ""

    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        getCurrentUserFromDB(currentUserId, database)
        messagesListRV = findViewById(R.id.messagesRecyclerView)

        // Retrieve messages query from DB
        val messageQuery = database.child("messages").limitToLast(50)
        val options = FirebaseRecyclerOptions.
                                                        Builder<Message>().
                                                        setQuery(messageQuery, Message::class.java).
                                                        build()

        adapter = MessageListAdapter(options)

        messagesListRV.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messagesListRV.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        //auth.signOut()
    }

    fun sendMessage(view: View) {
        val inputMessage = findViewById<EditText>(R.id.inputMessage)

        if (inputMessage.text.isNotEmpty()) {
            val message = Message(
                messageBody = inputMessage.text.toString(),
                userId = currentUserId,
                userName = currentUserName
            ).toMap()

            // Clear input text field
            inputMessage.setText("")

            val key = database.child("messages").push().key
            database.updateChildren(mutableMapOf<String, Any>("/messages/$key" to message))
        }
    }

    private fun getCurrentUserFromDB(userId: String?, database: DatabaseReference) {
        class valueEventListener(): ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.children.toList()[0].value as MutableMap<*,*>
                currentUserName = user["userName"] as String?
                println(Calendar.getInstance().get(Calendar.HOUR).toString())
            }
        }

        val retrieveUserEventListener = valueEventListener()
        val userQuery = database.child("users").orderByChild("userId").equalTo(userId)
        userQuery.addListenerForSingleValueEvent(retrieveUserEventListener)
        println(currentUserName)
    }
}