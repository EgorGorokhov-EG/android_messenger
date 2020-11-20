package com.eg.messenger

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DialogActivity : AppCompatActivity() {

    private val database = Firebase.database.reference
    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        messagesListRV = findViewById(R.id.messagesRecyclerView)
    }

    override fun onStart() {
        super.onStart()
        val messageQuery = database.child("messages").limitToLast(10)
        val options = FirebaseRecyclerOptions.
                                                                Builder<Message>().
                                                                setQuery(messageQuery, Message::class.java).
                                                                build()

        adapter = MessageListAdapter(options)
        adapter.startListening()

        messagesListRV.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messagesListRV.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    fun sendMessage(view: View) {
        val inputMessage = findViewById<EditText>(R.id.inputMessage)

        if (inputMessage.text.isNotEmpty()) {
            val message = Message(messageBody = inputMessage.text.toString()).toMap()
            inputMessage.setText("")

            val key = database.child("messages").push().key

            database.updateChildren(mutableMapOf<String, Any>("/messages/$key" to message))
        }
    }
}