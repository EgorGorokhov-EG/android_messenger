package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.FirebaseAuthKtxRegistrar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.view.*

const val MSG_TYPE_SENT = 0
const val MSG_TYPE_RECEIVED = 1

class MessageListAdapter(private val options: FirebaseRecyclerOptions<Message>, private val currentUserId: String?):
    FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>(options){

    private lateinit var parentRV: RecyclerView
    private val auth = Firebase.auth

    // Inflate view holder with specified layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == MSG_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_message_layout, parent, false)
            MessageViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.received_message_layout, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
       return if (options.snapshots[position].userId == currentUserId) MSG_TYPE_SENT
            else  MSG_TYPE_RECEIVED
    }

    // Bind data from Message to view holder to display it in the layout
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.bindMessage(model)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentRV = recyclerView
    }

    override fun onDataChanged() {
        super.onDataChanged()
        parentRV.smoothScrollToPosition(this.itemCount)
    }

    class MessageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val messageBody = view.findViewById<TextView>(R.id.textMessageBody)
        private val timeMessageSent = view.findViewById<TextView>(R.id.timeMessageSent)

        fun bindMessage(message: Message) {
            messageBody.text = message.messageBody
            timeMessageSent.text = message.createdAt
        }
    }
}
