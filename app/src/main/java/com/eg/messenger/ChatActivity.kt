package com.eg.messenger

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private val currentUserId = auth.currentUser?.uid
    private var currentUserName: String? = ""

    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    private val localCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        currentUserName = getCurrentUserFromDB(currentUserId, database)
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
            val createdAtHour = localCalendar.get(Calendar.HOUR)
            val createdAtMinutes = localCalendar.get(Calendar.MINUTE)
            val timeAmPm = if (localCalendar.get(Calendar.AM_PM) == 0) "AM" else "PM"

            val message = Message(
                messageBody = inputMessage.text.toString(),
                userId = currentUserId,
                userName = currentUserName,
                createdAt = "$createdAtHour:$createdAtMinutes $timeAmPm"
            ).toMap()

            // Clear input text field
            inputMessage.setText("")

            // Update messages in the DB
            val key = database.child("messages").push().key
            database.updateChildren(mutableMapOf<String, Any>("/messages/$key" to message))
        }
    }
}