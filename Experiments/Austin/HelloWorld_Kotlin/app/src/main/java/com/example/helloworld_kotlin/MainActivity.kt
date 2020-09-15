package com.example.helloworld_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val b_toast = findViewById<Button>(R.id.b_toast)
        //incomplete

        fun toastMe(view: View) {
            val myToast = Toast.makeText(this, "Hello! Want some Toast?", Toast.LENGTH_LONG)
            myToast.show()
        }
    }
}
