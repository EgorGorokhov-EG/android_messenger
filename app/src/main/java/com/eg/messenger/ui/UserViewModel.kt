package com.eg.messenger.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eg.messenger.ChatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserViewModel: ViewModel() {

    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?>
        get() = _currentUserId

    private val _currentUsername = MutableLiveData<String?>()
    val currentUsername: LiveData<String?>
        get() =_currentUsername

    private val _currentChatId = MutableLiveData<String?>()
    val currentChatId: LiveData<String?>
        get() = _currentChatId

    private fun updateUserIdByAuthId() {
        val authId = auth.currentUser?.uid

        val getUserIdQuery = database.child("users").orderByChild("authId").equalTo(authId)
        getUserIdQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val snapValue = snapshot.value
                _currentUserId.postValue((snapValue as HashMap<*,*>).keys.toList()[0] as String?)
                println("user id from model: $_currentUserId")
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateUsername() {
        val getUsernameQuery = database.child("users").orderByKey().equalTo(_currentUserId.value as String)
        getUsernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                _currentUserId.postValue((snapshot.value as HashMap<*,*>)["userName"] as String?)
                println("username from model: $_currentUsername")
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun loadInfo() {
    }
}