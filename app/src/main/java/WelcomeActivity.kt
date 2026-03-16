package com.example.mobilequizapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Pasek na górze z nazwą aplikacji będzie tu automatycznie dzięki ustawieniom z Krok 1
    }
}