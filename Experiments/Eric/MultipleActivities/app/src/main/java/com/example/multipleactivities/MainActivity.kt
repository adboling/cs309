package com.example.multipleactivities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Option1.setOnClickListener({
            var clickIntent = Intent (this@MainActivity, Option1Activity::class.java)
            startActivity(clickIntent)
        })

        Option2.setOnClickListener({
            var clickIntent = Intent(this@MainActivity, Option2Activity::class.java)
            startActivity(clickIntent)
        })
    }
}
