package com.example.guill.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    public TextView textViewResult;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        editText = (EditText) findViewById(R.id.editText);

    }

    public void startAnalyze(View v) {

        startButton.setActivated(!startButton.isActivated());

        changeButtonText();

        analyzeText();
    }

    private void analyzeText() {

        ParseText parseText = new ParseText(this.editText.getText().toString(), (double) 6);
        double wordsPerMinute = parseText.wordsPerMinute();
        ArrayList<Word> list = new ArrayList<Word>();
        list = parseText.getMostRepeted(30);
       // String analyseResult = AnalyseText(content);
        String wordsRepeted = "";

        for (Word word: list) {
            wordsRepeted += word.word + ": " + word.iteration + "\n";
        }

        textViewResult.setText("Mots par minute : " + String.valueOf(wordsPerMinute) + "\n"
                + "Mots les plus utilisés : \n" + wordsRepeted);
    }

    private void changeButtonText() {
        if(startButton.isActivated()) {
            startButton.setText("Stop recording");
        }
        else {
            startButton.setText("Start recording");
        }
    }

    private String AnalyseText(String myText) {
        myText = CleanText(myText);
        System.out.println(myText);
        return "result";
    }

    private String CleanText(String myText) {
        return myText.replaceAll("[-+.^:,]","");
    }


}

