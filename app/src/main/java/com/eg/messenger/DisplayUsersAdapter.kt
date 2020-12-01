package com.eg.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eg.messenger.data.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DisplayUsersAdapter(options: FirestoreRecyclerOptions<User>,
                          private val listener: (User) -> Unit):
FirestoreRecyclerAdapter<User, DisplayUsersAdapter.UserViewHolder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
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