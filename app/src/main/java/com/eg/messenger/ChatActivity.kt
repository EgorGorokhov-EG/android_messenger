package com.eg.messenger

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.Message
import com.eg.messenger.data.User
import com.eg.messenger.notification.NotificationData
import com.eg.messenger.notification.PushNotification
import com.eg.messenger.notification.RetrofitInstance
import com.eg.messenger.ui.ChatViewModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ChatActivity : MenuActivity() {
    private val TAG = "ChatActivity"

    private val db = Firebase.firestore

    private var currentChatId: String? = ""

    private var currentUserId: String? = ""

    private var anotherUserId: String? = ""

    private val viewModel: ChatViewModel by viewModels()

    private lateinit var messagesListRV: RecyclerView
    private lateinit var adapter: FirestoreRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>

    private val localCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val extras = intent.extras
        currentUserId = extras?.getString("currentUserId")
        anotherUserId = extras?.getString("anotherUserId")
        currentChatId = extras?.getString("currentChatId")

        viewModel.updateAnotherUser(anotherUserId)
        viewModel.updateCurrentUser(currentUserId)

        messagesListRV = findViewById(R.id.messagesRecyclerView)

        // Retrieve messages query from DB
        val messageQuery =
            db.collection("chats")
            .document(currentChatId as String)
            .collection("messages")
            .orderBy("timestamp")
            .limit(50)

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

        // Update last message in this chat
        val chatRef = db.collection("chats").document(currentChatId as String)

        //TODO: Make UI to update when last message changes(in DisplayChatsActivity)
        chatRef.collection("messages")
            .orderBy("timestamp")
            .limitToLast(1)
            .get().addOnSuccessListener {
                chatRef.update("lastMessage", it.toObjects(Message::class.java))
            }
    }

    fun sendMessage(view: View) {
        val inputMessage = findViewById<EditText>(R.id.inputMessage)

        if (inputMessage.text.isNotEmpty()) {
            //TODO:
            // Make right format for 12PM(now it displays as 0 not as 12)
            // Change format for minutes < 10 (now it displays as 4:7 not as 4:07)
            val createdAtHour = localCalendar.get(Calendar.HOUR)
            val createdAtMinutes = localCalendar.get(Calendar.MINUTE)
            val timeAmPm = if (localCalendar.get(Calendar.AM_PM) == 0) "AM" else "PM"

            val newMessage = Message(
                messageBody = inputMessage.text.toString(),
                userId = currentUserId,
                userName = viewModel.currentUser.value?.userName,
                createdAt = "$createdAtHour:$createdAtMinutes $timeAmPm"
            )

            // Clear input text field
            inputMessage.setText("")

            // Send push notification to another user in chat
            PushNotification(NotificationData(newMessage.userName, newMessage.messageBody), viewModel.anotherUser.value?.messagingToken).also {
                sendNotification(it)
            }

            // Update this chat messages in the DB
            db.collection("chats").document(currentChatId as String).collection("messages").add(newMessage).addOnSuccessListener {
                it.update(hashMapOf<String, Any>("timestamp" to FieldValue.serverTimestamp()))
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}