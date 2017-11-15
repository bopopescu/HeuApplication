package com.example.guill.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    TextView textViewSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);

        String speechResult = getIntent().getStringExtra("speechResult");
        Double totalTime = getIntent().getDoubleExtra("totalTime", 0);
        System.out.println("Result : " + speechResult);
        System.out.println("Total Time : " + totalTime);

        ParseText parseText = new ParseText(speechResult, totalTime);

        int wordsPerMinute = parseText.wordsPerMinute();







        textViewSpeed.setText("Mot par minute : " + String.valueOf(wordsPerMinute));

    }
}
