package com.example.navigation_drawer_test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_hello.*

class HelloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        updateTextButton.setOnClickListener {welcomeTextView.text = "Hello Kotlin World!" }
    }
}
