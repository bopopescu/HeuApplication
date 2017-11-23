package com.example.guill.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class ShowText extends AppCompatActivity {

    TextView textViewShowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_text);

        textViewShowText = (TextView) findViewById(R.id.textViewShowText);
        textViewShowText.setMovementMethod(new ScrollingMovementMethod());

        final String speechResult = getIntent().getStringExtra("speechResult");
        textViewShowText.setText(speechResult);
    }
}
