package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatsListAdapter(options: FirebaseRecyclerOptions<Chat>):
    FirebaseRecyclerAdapter<Chat, ChatsListAdapter.ChatViewHolder>(options) {

    val auth = Firebase.auth

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
        holder.bindDialog(model)
    }

    class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val chatItemUsername = view.findViewById<TextView>(R.id.chatUserName)
        val chatLastMessage = view.findViewById<TextView>(R.id.chatLastMessage)
        val chatTimeLastMessageSent = view.findViewById<TextView>(R.id.chatTimeLastMessageSent)

        fun bindDialog(dialog: Chat) {
            chatItemUsername.text = dialog.user1
        }
    }

}