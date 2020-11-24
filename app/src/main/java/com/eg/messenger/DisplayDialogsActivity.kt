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

class DisplayDialogsActivity : AppCompatActivity() {
    lateinit var addNewDialogFAB: FloatingActionButton

    val database = Firebase.database.reference
    val auth = Firebase.auth

    lateinit var dialogsAdapter: DialogsListAdapter

    private val currentUserId = auth.currentUser?.uid
    private var currentUserName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dialogs)

        currentUserName = getCurrentUserFromDB(currentUserId, database)

        val dialogsQuery = database.child("dialogs")
        val options = FirebaseRecyclerOptions.Builder<Dialog>().setQuery(dialogsQuery, Dialog::class.java).build()

        val dialogsRV = findViewById<RecyclerView>(R.id.dialogsRecyclerView)
        dialogsAdapter = DialogsListAdapter(options)

        dialogsRV.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = dialogsAdapter
        }

        addNewDialogFAB = findViewById(R.id.addNewDialogBtn)
        addNewDialogFAB.setOnClickListener { view: View ->
            val key = database.child("dialogs").push().key

            val newChat = Dialog("testUser${Random.nextInt()}", "testUser2").toMap()
            database.updateChildren(mutableMapOf<String, Any?>("/dialogs/$key" to newChat))
        }
    }

    override fun onStart() {
        super.onStart()
        dialogsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        dialogsAdapter.stopListening()
    }
}