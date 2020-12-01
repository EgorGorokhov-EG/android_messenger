package com.eg.messenger

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatActivity : MenuActivity() {

    private val db = Firebase.firestore
    //private val auth = Firebase.auth

    private var currentChatId: String? = ""

    private var currentUserId: String? = ""
    private var currentUserName: String? = ""

    private var anotherUserId: String? = ""

    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirestoreRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    private val localCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val extras = intent.extras
        currentUserId = extras?.getString("currentUserId")
        anotherUserId = extras?.getString("anotherUserId")

        messagesListRV = findViewById(R.id.messagesRecyclerView)

        // Retrieve messages query from DB
        val messageQuery = db.collection("chats").document(currentChatId as String).collection("messages")
        val options = FirestoreRecyclerOptions.
                                            Builder<Message>().
                                            setQuery(messageQuery, Message::class.java).
                                            build()

        adapter = MessageListAdapter(options, currentUserId)
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
            )

            // Clear input text field
            inputMessage.setText("")

            // Update messages in the DB
            db.collection("chats").document(currentChatId as String).collection("messages").add(message)
        }
    }
}