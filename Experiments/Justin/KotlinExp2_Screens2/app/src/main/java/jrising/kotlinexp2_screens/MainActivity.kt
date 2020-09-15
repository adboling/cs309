package jrising.kotlinexp2_screens

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    var btn = findViewById<Button>(R.id.button1)
        btn.setOnClickListener({

            val clickIntent = Intent (this, Screen2::class.java)
            startActivity(clickIntent)
        })
    }
}

