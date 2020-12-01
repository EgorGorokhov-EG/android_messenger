package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.Chat
import com.eg.messenger.data.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatsListAdapter(
    options: FirestoreRecyclerOptions<Chat>,
    private val openChat: (Chat) -> Unit):
    FirestoreRecyclerAdapter<Chat, ChatsListAdapter.ChatViewHolder>(options) {

    private val authId = Firebase.auth.currentUser?.uid
    val db = Firebase.firestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int,
        model: Chat
    ) {
        val anotherUserId = model.users.filterNot { it == authId }[0].toString()
        db.document("users/$anotherUserId").get().addOnSuccessListener {
            val recipientUsername = it.toObject(User::class.java)?.userName
            holder.bindChat(model, recipientUsername)
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            openChat(model)
        })
    }

    class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val chatItemUsername = view.findViewById<TextView>(R.id.chatUserName)
        private val chatLastMessage = view.findViewById<TextView>(R.id.chatLastMessage)
        private val chatTimeLastMessageSent = view.findViewById<TextView>(R.id.chatTimeLastMessageSent)

        fun bindChat(model: Chat, recipientUsername: String?) {
            chatItemUsername.text = recipientUsername
            chatLastMessage.text = model.lastMessage[0]["messageBody"] as String
            chatTimeLastMessageSent.text = model.lastMessage[0]["createdAt"] as String
        }
    }
}