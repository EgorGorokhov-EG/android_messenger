package com.eg.messenger

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class MenuActivity : AppCompatActivity() {
    private val auth = Firebase.auth

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menuItemSignOut -> {
                signOutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOutUser() {
        auth.signOut()
        val intent = Intent(this, AuthenticationActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}