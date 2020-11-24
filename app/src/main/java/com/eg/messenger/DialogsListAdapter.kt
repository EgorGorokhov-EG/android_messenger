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

class DialogsListAdapter(options: FirebaseRecyclerOptions<Dialog>):
    FirebaseRecyclerAdapter<Dialog, DialogsListAdapter.ChatViewHolder>(options) {

    val auth = Firebase.auth

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_item_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int,
        model: Dialog
    ) {
        holder.bindDialog(model)
    }

    class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val dialogItemUsername = view.findViewById<TextView>(R.id.dialogUserName)
        val dialogLastMessage = view.findViewById<TextView>(R.id.dialogLastMessage)
        val dialogTimeLastMessageSent = view.findViewById<TextView>(R.id.dialogTimeLastMessageSent)

        fun bindDialog(dialog: Dialog) {
            dialogItemUsername.text = dialog.user1
            println(dialogItemUsername.text)
        }
    }

}