package com.example.helloworld_java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    EditText et_min, et_max;
    Button b_generate;
    TextView tv_output;

    Random rand;
    int min, max, output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rand = new Random();

        et_min = (EditText) findViewById(R.id.et_min);
        et_max = (EditText) findViewById(R.id.et_max);
        b_generate = (Button) findViewById(R.id.b_generate);
        tv_output = (TextView) findViewById(R.id.tv_output);

        b_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testMin, testMax;
                testMin = et_min.getText().toString();
                testMax = et_max.getText().toString();

                if(!testMin.equals("") && !testMax.equals("")) {
                    min = Integer.parseInt(testMin);
                    max = Integer.parseInt(testMax);
                    if(min < max) {
                        output = rand.nextInt((max - min) + 1) + min;
                        tv_output.setText("" + output);
                    }
                    else {
                        tv_output.setText("Input Min >= Input Max");
                    }
                }
                else {
                    tv_output.setText("Invalid Input");
                }
            }
        });
    }
}
