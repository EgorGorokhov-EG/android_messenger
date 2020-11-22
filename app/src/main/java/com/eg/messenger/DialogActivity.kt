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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DialogActivity : AppCompatActivity() {

    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private val currentUserId = auth.currentUser?.uid
    private val currentUserName = auth.currentUser?.displayName

    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        messagesListRV = findViewById(R.id.messagesRecyclerView)

        // Retrieve messages query from DB
        val messageQuery = database.child("messages").limitToLast(50)
        val options = FirebaseRecyclerOptions.
                                                            Builder<Message>().
                                                            setQuery(messageQuery, Message::class.java).
                                                            build()

        adapter = MessageListAdapter(options)
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in
        if (auth.currentUser == null) {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
        }
        else {
            adapter.startListening()

            messagesListRV.layoutManager = LinearLayoutManager(this).apply {
                stackFromEnd = true
            }
            messagesListRV.adapter = adapter}
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        auth.signOut()
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
}