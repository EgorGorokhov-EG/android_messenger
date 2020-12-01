package com.eg.messenger

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.Chat
import com.eg.messenger.ui.UserViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DisplayChatsActivity : MenuActivity() {

    private val db = Firebase.firestore

    private val auth = Firebase.auth
    private val currentAuthId = auth.currentUser?.uid

    private val userModel: UserViewModel by viewModels()

    private lateinit var chatsAdapter: ChatsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chats)

        val chatsRV = findViewById<RecyclerView>(R.id.chatsRecyclerView)

        val chatsQuery = db.collection("chats")
        val options = FirestoreRecyclerOptions.Builder<Chat>().setQuery(chatsQuery, Chat::class.java).build()

        val openChat = {chat: Chat ->
            val anotherUserId = chat.users.filterNot { it == currentAuthId }[0]

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("currentUserId", currentAuthId)
                putExtra("anotherUserId", anotherUserId)
            }
            startActivity(intent)
        }


        chatsAdapter = ChatsListAdapter(options, openChat)
        chatsRV.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = chatsAdapter
        }


       // Handle creation of a new Chat via FAB
        val addNewChatFAB: FloatingActionButton = findViewById(R.id.addNewChatBtn)
        addNewChatFAB.setOnClickListener {
            val intent = Intent(this, CreateNewChatActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NO_HISTORY
                putExtra("currentUserId", currentAuthId)
            }
            startActivity(intent)
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