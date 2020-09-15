package a27madlads.exp1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myBtn = findViewById<Button>(R.id.Mybutton);
        val myTxt = findViewById<TextView>(R.id.myTextView);
        myBtn.setOnClickListener {
            //displays a toast
            Toast.makeText(this, "Button Was Clicked", Toast.LENGTH_LONG).show();
            //change text of TextView
            myTxt.text = "Hello Kotlin World";
        }
    }
    //Run app

}
