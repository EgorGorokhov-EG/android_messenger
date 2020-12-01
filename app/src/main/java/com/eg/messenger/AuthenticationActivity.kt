package com.eg.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.eg.messenger.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            val intent = Intent(this, DisplayChatsActivity::class.java).also {
                startActivity(it)
            }
        }
        auth.signOut()
    }

    fun signInUser(view: View) {
        val email = findViewById<EditText>(R.id.emailInput).text.toString()
        val password = findViewById<EditText>(R.id.passwordInput).text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) startActivity(Intent(this, DisplayChatsActivity::class.java))
            else Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun signUpUser(view: View) {
        val email = findViewById<EditText>(R.id.emailInput).text.toString()
        val password = findViewById<EditText>(R.id.passwordInput).text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
        {
            task ->
                if (task.isSuccessful) {
                    val authId = auth.currentUser?.uid as String
                    val newUser = User(authId, userName = email, email = email)

                    db.collection("users").document(authId).set(newUser).addOnSuccessListener {
                        val intent = Intent(this, DisplayChatsActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                }
                else Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }
}