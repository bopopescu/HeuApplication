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

        //int wordsPerMinute = getIntent().getIntExtra("wordsPerMinute", 0);
        //ArrayList<Word> list = getIntent().getParcelableArrayListExtra("list");
        ParseText parseText = (ParseText) getIntent().getSerializableExtra("ParseText");
        System.out.println(String.valueOf(parseText.wordsPerMinute()));
        //textViewSpeed.setText("Mots par minute : " + String.valueOf(wordsPerMinute));

    }
}
