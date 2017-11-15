package com.example.guill.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView textViewSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);

        String speechResult = getIntent().getStringExtra("speechResult");
        int totalTime = getIntent().getIntExtra("totalTime", 0);
        System.out.println("Result : " + speechResult);
        System.out.println("Total Time : " + totalTime);

        ParseText parseText = new ParseText(speechResult, totalTime);
        
        int wordsPerMinute = parseText.wordsPerMinute();

        textViewSpeed.setText("Mot par minute : " + String.valueOf(wordsPerMinute));

    }
}
