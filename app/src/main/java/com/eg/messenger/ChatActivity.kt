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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : MenuActivity() {

    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private var currentChatId: String? = ""

    private var currentUserId: String? = ""
    private var currentUserName: String? = ""

    private var anotherUserId: String? = ""


    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    private val localCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intentExtras = intent.extras
        currentUserName = intentExtras?.getString("currentUserId")
        anotherUserId = intentExtras?.getString("anotherUserId")
        currentChatId = intentExtras?.getString("chatId")

        messagesListRV = findViewById(R.id.messagesRecyclerView)

        // Retrieve messages query from DB
        val messageQuery = database.child("messages").child(currentChatId as String).limitToLast(50)
        val options = FirebaseRecyclerOptions.
                                            Builder<Message>().
                                            setQuery(messageQuery, Message::class.java).
                                            build()

        adapter = MessageListAdapter(options)
        messagesListRV.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messagesListRV.adapter = adapter

        // Retrieve current userName frm the DB
        val getUserNameQuery = database.child("users").orderByKey().equalTo(currentUserId)
        getUserNameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.value as HashMap<*,*>
                currentUserName = user["userName"] as String?
                println("User name: $currentUserName")
            }
            override fun onCancelled(error: DatabaseError) {}
        })
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
            ).toMap()

            // Clear input text field
            inputMessage.setText("")

            // Update messages in the DB
            val key = database.child("messages").child(currentChatId as String).push().key
            database.updateChildren(mutableMapOf<String, Any>("/messages/$key" to message))
        }
    }
}