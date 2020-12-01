package com.eg.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.Chat
import com.eg.messenger.data.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateNewChatActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var usersRV: RecyclerView
    private lateinit var usersAdapter: DisplayUsersAdapter

    private var currentUserId: String? = ""
    private var anotherUserId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_chat)

        currentUserId = intent.extras?.getString("currentUserId")
        println("userId from create new chat: $currentUserId")
        usersRV = findViewById(R.id.anotherUsersRV)

        val usersQuery = db.collection("users").whereNotEqualTo("userId", currentUserId)
        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(usersQuery, User::class.java).build()

        val listener = {user: User ->
            // On click create new chat with current user and chosen one
            // Also update these users
            // And then start ChatActivity

            anotherUserId = user.userId
            val chatId = currentUserId + anotherUserId
            val newChat = Chat(chatId, listOf(currentUserId, anotherUserId))

            // Update user's info in the DB
            val databaseUsers = db.collection("users")
            databaseUsers.document(currentUserId as String).update("chats", FieldValue.arrayUnion(chatId))
            databaseUsers.document(anotherUserId as String).update("chats", FieldValue.arrayUnion(chatId))

            // Create new chat in the DB
            db.collection("chats").document(chatId).set(newChat)

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("chatId", chatId)
                putExtra("currentUserId", currentUserId)
                putExtra("anotherUserId", anotherUserId)
            }
            startActivity(intent)
        }

        usersAdapter = DisplayUsersAdapter(options, listener)
        usersRV.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = usersAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        usersAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        usersAdapter.stopListening()
    }
}