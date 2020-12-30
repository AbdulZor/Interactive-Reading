package com.example.interactivereading.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.interactivereading.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth

        Handler(Looper.getMainLooper()).postDelayed({
            handleUserSignedInCheck()
            finish() // will pop this activity from back stack
        }, 1200)
    }

    private fun handleUserSignedInCheck() {
        // If activity is started, but user is not yet authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // If user is authenticated
        startActivity(Intent(this, MainActivity::class.java))
    }
}