package com.eg.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class DisplayChatsActivity : AppCompatActivity() {
    private lateinit var addNewChatFAB: FloatingActionButton

    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    lateinit var chatsAdapter: ChatsListAdapter

    private val currentUserId = auth.currentUser?.uid
    private var currentUserName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chats)

        currentUserName = getCurrentUserFromDB(currentUserId, database)

        val chatsQuery = database.child("chats")
        val options = FirebaseRecyclerOptions.Builder<Chat>().setQuery(chatsQuery, Chat::class.java).build()

        val chatsRV = findViewById<RecyclerView>(R.id.chatsRecyclerView)
        chatsAdapter = ChatsListAdapter(options)

        chatsRV.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = chatsAdapter
        }

        addNewChatFAB = findViewById(R.id.addNewChatBtn)
        addNewChatFAB.setOnClickListener { view: View ->
            val dialogId = database.child("chats").push().key

            val newChat = Chat("testUser${Random.nextInt()}", "testUser2").toMap()
            database.updateChildren(mutableMapOf<String, Any?>("/chats/$dialogId" to newChat))
        }
    }

    override fun onStart() {
        super.onStart()
        chatsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatsAdapter.stopListening()
    }
}