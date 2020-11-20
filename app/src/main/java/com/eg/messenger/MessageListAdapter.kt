package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MessageListAdapter(options: FirebaseRecyclerOptions<Message>):
    FirebaseRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>(options){

    private lateinit var parentRV: RecyclerView

    // Inflate view holder with specified layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_message_layout, parent, false)
        return MessageViewHolder(view)
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

        fun bindMessage(message: Message) {
            messageBody.text = message.messageBody
        }
    }
}
