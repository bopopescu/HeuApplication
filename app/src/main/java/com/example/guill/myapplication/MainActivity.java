package com.example.guill.myapplication;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    Button synonymeButton;
   // Button recordButton;
    TextView textViewResult;
    TextView onomatopoeiaTextView;
    TextView synonymeTextView;
    EditText editText;

    MediaRecorder recorder = new MediaRecorder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recordButton = (Button) findViewById(R.id.recordButton);
        startButton = (Button) findViewById(R.id.startButton);
        synonymeButton = (Button) findViewById(R.id.synonymeButton);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        onomatopoeiaTextView = (TextView) findViewById(R.id.onomatopoeiaTextView);
        editText = (EditText) findViewById(R.id.editText);
        synonymeTextView = (TextView) findViewById(R.id.synonymeTextView);

        this.synonymeTextView.bringToFront();
        this.synonymeTextView.setVisibility(this.synonymeTextView.INVISIBLE);

    }

    public void startAnalyze(View v) {

        startButton.setActivated(!startButton.isActivated());

        //changeButtonText();

        analyzeText();
    }

    public void synonymePressed(View v) {

        if(this.synonymeButton.isActivated() == false) {
            this.synonymeButton.setText("Hide synonymes");
            synonymeButton.setActivated(true);

            this.synonymeTextView.setVisibility(this.synonymeTextView.VISIBLE);
            this.startButton.setVisibility(this.startButton.INVISIBLE);

        }
        else {
            this.synonymeButton.setActivated(false);
            this.synonymeButton.setText("Show synonymes");
            this.synonymeTextView.setVisibility(this.synonymeTextView.INVISIBLE);
            this.startButton.setVisibility(this.startButton.VISIBLE);
        }
    }

    private void analyzeText() {

        ParseText parseText = new ParseText(this.editText.getText().toString(), (double) 6);
        double wordsPerMinute = parseText.wordsPerMinute();

        ArrayList<Word> list = new ArrayList<Word>();
        ArrayList<Word> onomatopoeiaList = new ArrayList<Word>();

        list = parseText.getMostRepeted(5);
        onomatopoeiaList = parseText.getMostRepetedOnomatopoeia(20);
       // String analyseResult = AnalyseText(content);
        String wordsRepeted = "";
        String synonymeText = "SYNONYMES PROPOSÉS";
        for (Word word: list) {
            Log.d("synonyme list", "" + word.synonymeList.size());
            wordsRepeted += word.word + ": " + word.iteration + "\n";
            synonymeText += word.getSynonyme();
        }

        synonymeTextView.setText(synonymeText);

        textViewResult.setText("Mots par minute : " + String.valueOf(wordsPerMinute) + "\n"
                + "Mots les plus utilisés : \n" + wordsRepeted);

        String onomatopoeiaRepeted = "";

        for (Word word: onomatopoeiaList) {
            onomatopoeiaRepeted += word.word + ": " + word.iteration + "\n";
        }

        onomatopoeiaTextView.setText("Onomatopées les plus utilisés : \n" + onomatopoeiaRepeted);

    }

   /* private void changeButtonText() {
        if(recordButton.isActivated()) {
            recordButton.setText("Stop recording");
        }
        else {
            recordButton.setText("Start recording");
        }
    }*/

    private void startRecording() {

       // changeButtonText();
        String status = Environment.getExternalStorageState();
        /*if(status.equals("mounted")){
            String path = your path;
        }*/

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/sdcard/.voicerecorder/voices");
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
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

