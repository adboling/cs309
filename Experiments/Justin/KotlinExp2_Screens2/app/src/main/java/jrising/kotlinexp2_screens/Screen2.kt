package jrising.kotlinexp2_screens

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout


class Screen2 : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen2)

var temp = 1

        var Mybutton = findViewById<Button>(R.id.cButton)
        Mybutton.setOnClickListener({
            val background = findViewById<RelativeLayout>(R.id.linear_layout)
            if(temp == 1) {
                val background = findViewById<RelativeLayout>(R.id.linear_layout)
                background.setBackgroundColor(Color.GREEN)
                temp = 2
            }
            if(temp == 2){
                background.setBackgroundColor(Color.MAGENTA)
                temp = 1

            }


        })

    }
}


330