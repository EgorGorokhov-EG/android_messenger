package com.eg.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CreateNewChatActivity : AppCompatActivity() {
    private val database = Firebase.database.reference

    private lateinit var usersRV: RecyclerView
    private lateinit var usersAdapter: DisplayUsersAdapter

    private var currentUserId: String? = ""
    private var anotherUserId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_chat)

        currentUserId = intent.extras?.getString("currentUserId")
        usersRV = findViewById(R.id.anotherUsersRV)

        val usersQuery = database.child("users").orderByChild(currentUserId as String).equalTo(null)
        val options = FirebaseRecyclerOptions.Builder<User>().setQuery(usersQuery, User::class.java).build()

        val listener = {user: User ->
            // On click create new chat with current user and chosen one
            // Also update these users
            // And then start ChatActivity

            println(user.authId)
            // Retrieve current userID from the DB
            val getUserIdQuery = database.child("users").orderByChild("authId").equalTo(user.authId)
            getUserIdQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val snapValue = snapshot.value
                    println(snapValue.toString())
                    anotherUserId = (snapValue as HashMap<*,*>).keys.toList()[0] as String?

                }
                override fun onCancelled(error: DatabaseError) {}
            })

            val chatId = database.child("chats").push().key

            // Update user's info in the DB
            println("Another userId onDataChange $anotherUserId")
            val databaseUsers = database.child("users")
            databaseUsers.child(currentUserId as String).child("chats").child(chatId as String).setValue(true)
            databaseUsers.child(anotherUserId as String).child("chats").child(chatId as String).setValue(true)

            // Create new chat in the DB
            val newChat = Chat(mutableMapOf(currentUserId to true, anotherUserId to true)).toMap()
            database.updateChildren(mutableMapOf<String, Any>("/chats/$chatId" to newChat))

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("chatId", chatId)
                putExtra("currentUserId", currentUserId)
                putExtra("anotherUserId", anotherUserId)
            }
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