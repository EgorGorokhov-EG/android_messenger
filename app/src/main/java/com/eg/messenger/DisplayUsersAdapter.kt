package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class DisplayUsersAdapter(options: FirebaseRecyclerOptions<User>, val listener: (User) -> Unit):
FirebaseRecyclerAdapter<User, DisplayUsersAdapter.UserViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisplayUsersAdapter.UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DisplayUsersAdapter.UserViewHolder,
        position: Int,
        model: User
    ) {
        holder.bind(model)
        holder.itemView.setOnClickListener { listener(model) }
    }

    class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val userName = view.findViewById<TextView>(R.id.userName)

        fun bind(user: User) {
            userName.text = user.userName
        }
    }

}