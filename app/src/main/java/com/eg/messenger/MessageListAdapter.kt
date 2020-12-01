package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val MSG_TYPE_SENT = 0
const val MSG_TYPE_RECEIVED = 1

class MessageListAdapter(private val options: FirestoreRecyclerOptions<Message>,
                         private val currentUserId: String?):
    FirestoreRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>(options){

    private lateinit var parentRV: RecyclerView

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
