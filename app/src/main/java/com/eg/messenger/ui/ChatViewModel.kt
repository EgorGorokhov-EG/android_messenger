package com.eg.messenger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eg.messenger.ChatActivity
import com.eg.messenger.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ChatViewModel: ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _anotherUser = MutableLiveData<User>()
    val anotherUser: LiveData<User>
        get() = _anotherUser

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _currentChatId = MutableLiveData<String?>()
    val currentChatId: LiveData<String?>
        get() = _currentChatId


    fun updateAnotherUser(anotherUserId: String?) {
        db.document("users/$anotherUserId").get().addOnSuccessListener {
            _anotherUser.setValue(it.toObject(User::class.java))
        }
    }

    fun updateCurrentUser(currentUserId: String?) {
        db.document("users/$currentUserId").get().addOnSuccessListener {
            _currentUser.setValue(it.toObject(User::class.java))
        }
    }
}