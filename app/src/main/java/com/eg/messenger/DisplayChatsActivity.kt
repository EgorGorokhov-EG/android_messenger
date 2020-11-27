package com.eg.messenger

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class DisplayChatsActivity : MenuActivity() {
    private lateinit var addNewChatFAB: FloatingActionButton

    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val currentAuthId = auth.currentUser?.uid

    private lateinit var chatsAdapter: ChatsListAdapter

    private var currentUserId: String? = ""
    private var currentUserName: String? = ""

    init {
        // Retrieve current userID from the DB
        val getUserIdQuery = database.child("users").orderByChild("authId").equalTo(currentAuthId)
        getUserIdQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val snapValue = snapshot.value
                currentUserId = (snapValue as HashMap<*,*>).keys.toList()[0] as String?
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chats)

        val chatsQuery = database.child("chats")
        val options = FirebaseRecyclerOptions.Builder<Chat>().setQuery(chatsQuery, Chat::class.java).build()

        val chatsRV = findViewById<RecyclerView>(R.id.chatsRecyclerView)
        chatsAdapter = ChatsListAdapter(options)

        chatsRV.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = chatsAdapter
        }

        addNewChatFAB = findViewById(R.id.addNewChatBtn)
        addNewChatFAB.setOnClickListener {
            println(currentUserId)
            val intent = Intent(this, CreateNewChatActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NO_HISTORY
                putExtra("currentUserId", currentUserId)
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